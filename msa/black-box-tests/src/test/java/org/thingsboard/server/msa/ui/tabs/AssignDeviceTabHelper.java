
package org.thingsboard.server.msa.ui.tabs;

import org.openqa.selenium.WebDriver;

public class AssignDeviceTabHelper extends AssignDeviceTabElements {
    public AssignDeviceTabHelper(WebDriver driver) {
        super(driver);
    }

    public void assignOnCustomer(String customerTitle) {
        assignOnCustomerField().click();
        customerFromDropDown(customerTitle).click();
        assignBtn().click();
    }
}
