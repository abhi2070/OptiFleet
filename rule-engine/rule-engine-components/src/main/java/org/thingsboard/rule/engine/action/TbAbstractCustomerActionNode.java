
package org.thingsboard.rule.engine.action;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.dao.customer.CustomerService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.thingsboard.common.util.DonAsynchron.withCallback;

@Slf4j
public abstract class TbAbstractCustomerActionNode<C extends TbAbstractCustomerActionNodeConfiguration> implements TbNode {

    protected C config;

    private LoadingCache<CustomerKey, Optional<CustomerId>> customerIdCache;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = loadCustomerNodeActionConfig(configuration);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        if (this.config.getCustomerCacheExpiration() > 0) {
            cacheBuilder.expireAfterWrite(this.config.getCustomerCacheExpiration(), TimeUnit.SECONDS);
        }
        customerIdCache = cacheBuilder
                .build(new CustomerCacheLoader(ctx, createCustomerIfNotExists()));
    }

    protected abstract boolean createCustomerIfNotExists();

    protected abstract C loadCustomerNodeActionConfig(TbNodeConfiguration configuration) throws TbNodeException;

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        withCallback(processCustomerAction(ctx, msg),
                m -> ctx.tellSuccess(msg),
                t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }

    private ListenableFuture<Void> processCustomerAction(TbContext ctx, TbMsg msg) {
        ListenableFuture<CustomerId> customerIdFeature = getCustomer(ctx, msg);
        return Futures.transform(customerIdFeature, customerId -> {
                    doProcessCustomerAction(ctx, msg, customerId);
                    return null;
                }, ctx.getDbCallbackExecutor()
        );
    }

    protected abstract void doProcessCustomerAction(TbContext ctx, TbMsg msg, CustomerId customerId);

    protected ListenableFuture<CustomerId> getCustomer(TbContext ctx, TbMsg msg) {
        String customerTitle = TbNodeUtils.processPattern(this.config.getCustomerNamePattern(), msg);
        CustomerKey key = new CustomerKey(customerTitle);
        return ctx.getDbCallbackExecutor().executeAsync(() -> {
            Optional<CustomerId> customerId = customerIdCache.get(key);
            if (!customerId.isPresent()) {
                throw new RuntimeException("No customer found with name '" + key.getCustomerTitle() + "'.");
            }
            return customerId.get();
        });
    }

    @Override
    public void destroy() {
        if (customerIdCache != null) {
            customerIdCache.invalidateAll();
        }
    }

    @Data
    @AllArgsConstructor
    private static class CustomerKey {
        private String customerTitle;
    }

    private static class CustomerCacheLoader extends CacheLoader<CustomerKey, Optional<CustomerId>> {

        private final TbContext ctx;
        private final boolean createIfNotExists;

        private CustomerCacheLoader(TbContext ctx, boolean createIfNotExists) {
            this.ctx = ctx;
            this.createIfNotExists = createIfNotExists;
        }

        @Override
        public Optional<CustomerId> load(CustomerKey key) {
            CustomerService service = ctx.getCustomerService();
            Optional<Customer> customerOptional =
                    service.findCustomerByTenantIdAndTitle(ctx.getTenantId(), key.getCustomerTitle());
            if (customerOptional.isPresent()) {
                return Optional.of(customerOptional.get().getId());
            } else if (createIfNotExists) {
                Customer newCustomer = new Customer();
                newCustomer.setTitle(key.getCustomerTitle());
                newCustomer.setTenantId(ctx.getTenantId());
                Customer savedCustomer = service.saveCustomer(newCustomer);
                ctx.enqueue(ctx.customerCreatedMsg(savedCustomer, ctx.getSelfId()),
                        () -> log.trace("Pushed Customer Created message: {}", savedCustomer),
                        throwable -> log.warn("Failed to push Customer Created message: {}", savedCustomer, throwable));
                return Optional.of(savedCustomer.getId());
            }
            return Optional.empty();
        }

    }

}
