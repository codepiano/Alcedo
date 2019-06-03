package com.codepiano.deduction.tool;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class NameTransferTest {

    @DataProvider
    public Object[][] transferData() {
        return new Object[][]{
                {"test_camel_case", "TestCamelCase"},
                {"_test_camel_case_", "TestCamelCase"},
                {"_test_camel_case_", "TestCamelCase"},
                {"test_Camel_case", "TestCamelCase"},
                {"test__camel_case", "TestCamelCase"},
                {"____", ""},
                {"", ""}};
    }


    @Test(dataProvider = "transferData")
    public void testTransferToCamelCase(String param, String result) {
        NameTransfer transfer = Mockito.mock(NameTransfer.class);
        Mockito.when(transfer.transferToCamelCase(param)).thenCallRealMethod();
        Assert.assertEquals(result, transfer.transferToCamelCase(param));
    }
}