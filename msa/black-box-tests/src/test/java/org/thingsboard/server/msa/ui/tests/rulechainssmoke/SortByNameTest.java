
package org.thingsboard.server.msa.ui.tests.rulechainssmoke;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.utils.DataProviderCredential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.thingsboard.server.msa.ui.utils.EntityPrototypes.defaultRuleChainPrototype;

@Feature("Sort rule chain by name")
public class SortByNameTest extends AbstractRuleChainTest {

    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description("Sort rule chain 'UP'")
    public void specialCharacterUp(String name) {
        ruleChainName = name;
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainName));

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameBtn().click();
        ruleChainsPage.setRuleChainName(0);

        assertThat(ruleChainsPage.getRuleChainName()).as("First rule chain after sort").isEqualTo(ruleChainName);
    }

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description("Sort rule chain 'UP'")
    public void allSortUp(String ruleChain, String ruleChainSymbol, String ruleChainNumber) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainSymbol));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChain));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainNumber));

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameBtn().click();
        ruleChainsPage.setRuleChainName(0);
        String firstRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(1);
        String secondRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(2);
        String thirdRuleChain = ruleChainsPage.getRuleChainName();

        deleteRuleChainByName(ruleChain);
        deleteRuleChainByName(ruleChainNumber);
        deleteRuleChainByName(ruleChainSymbol);

        assertThat(firstRuleChain).as("First rule chain with symbol in name").isEqualTo(ruleChainSymbol);
        assertThat(secondRuleChain).as("Second rule chain with number in name").isEqualTo(ruleChainNumber);
        assertThat(thirdRuleChain).as("Third rule chain with number in name").isEqualTo(ruleChain);
    }

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description("Sort rule chain 'DOWN'")
    public void specialCharacterDown(String ruleChainName) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainName));
        this.ruleChainName = ruleChainName;

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameDown();
        ruleChainsPage.setRuleChainName(ruleChainsPage.allNames().size() - 1);

        assertThat(ruleChainsPage.getRuleChainName()).as("Last rule chain after sort").isEqualTo(ruleChainName);
    }

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description("Sort rule chain 'DOWN'")
    public void allSortDown(String ruleChain, String ruleChainSymbol, String ruleChainNumber) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainSymbol));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChain));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainNumber));

        sideBarMenuView.ruleChainsBtn().click();
        int lastIndex = ruleChainsPage.allNames().size() - 1;
        ruleChainsPage.sortByNameDown();
        ruleChainsPage.setRuleChainName(lastIndex);
        String firstRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(lastIndex - 1);
        String secondRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(lastIndex - 2);
        String thirdRuleChain = ruleChainsPage.getRuleChainName();

        deleteRuleChainByName(ruleChain);
        deleteRuleChainByName(ruleChainNumber);
        deleteRuleChainByName(ruleChainSymbol);

        assertThat(firstRuleChain).as("First from the end rule chain with symbol in name").isEqualTo(ruleChainSymbol);
        assertThat(secondRuleChain).as("Second from the end rule chain with number in name").isEqualTo(ruleChainNumber);
        assertThat(thirdRuleChain).as("Third rule from the end chain with number in name").isEqualTo(ruleChain);
    }
}
