package org.thingsboard.server.dao.service.validator;

//@Component
public class RolesProfileDataValidator
//        extends DataValidator<RolesProfile>
{
//    @Autowired
//    private RolesProfileDao rolesProfileDao;
//    @Autowired
//    @Lazy
//    private RolesProfileService rolesProfileService;
//    @Autowired
//    private TenantService tenantService;
//    @Lazy
//    @Autowired
//    private QueueService queueService;
//    @Autowired
//    private RuleChainService ruleChainService;
//    @Autowired
//    private DashboardService dashboardService;
//
//    @Override
//    protected void validateDataImpl(TenantId tenantId, RolesProfile rolesProfile) {
//        validateString("Roles profile name", rolesProfile.getName());
//        if (rolesProfile.getTenantId() == null) {
//            throw new DataValidationException("Roles profile should be assigned to tenant!");
//        } else {
//            if (!tenantService.tenantExists(rolesProfile.getTenantId())) {
//                throw new DataValidationException("Roles profile is referencing to non-existent tenant!");
//            }
//        }
//        if (rolesProfile.isDefault()) {
//            RolesProfile defaultRolesProfile = rolesProfileService.findDefaultRolesProfile(tenantId);
//            if (defaultRolesProfile != null && !defaultRolesProfile.getId().equals(rolesProfile.getId())) {
//                throw new DataValidationException("Another default roles profile is present in scope of current tenant!");
//            }
//        }
//        if (StringUtils.isNotEmpty(rolesProfile.getDefaultQueueName())) {
//            Queue queue = queueService.findQueueByTenantIdAndName(tenantId, rolesProfile.getDefaultQueueName());
//            if (queue == null) {
//                throw new DataValidationException("Roles profile is referencing to non-existent queue!");
//            }
//        }
//
//        if (rolesProfile.getDefaultRuleChainId() != null) {
//            RuleChain ruleChain = ruleChainService.findRuleChainById(tenantId, rolesProfile.getDefaultRuleChainId());
//            if (ruleChain == null) {
//                throw new DataValidationException("Can't assign non-existent rule chain!");
//            }
//            if (!ruleChain.getTenantId().equals(rolesProfile.getTenantId())) {
//                throw new DataValidationException("Can't assign rule chain from different tenant!");
//            }
//        }
//
//        if (rolesProfile.getDefaultDashboardId() != null) {
//            DashboardInfo dashboard = dashboardService.findDashboardInfoById(tenantId, rolesProfile.getDefaultDashboardId());
//            if (dashboard == null) {
//                throw new DataValidationException("Can't assign non-existent dashboard!");
//            }
//            if (!dashboard.getTenantId().equals(rolesProfile.getTenantId())) {
//                throw new DataValidationException("Can't assign dashboard from different tenant!");
//            }
//        }
//    }
//
//    @Override
//    protected RolesProfile validateUpdate(TenantId tenantId, RolesProfile rolesProfile) {
//        RolesProfile old = rolesProfileDao.findById(rolesProfile.getTenantId(), rolesProfile.getId().getId());
//        if (old == null) {
//            throw new DataValidationException("Can't update non existing roles profile!");
//        }
//        return old;
//    }

}
