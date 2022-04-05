package com.dumpster_sensor.webapp;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.event.annotation.BeforeTestClass;

public class PasswordValidatorTests {

    @Test
    public void whenValidPassword_ThenNoError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors("@Ren1474");
        Assertions.assertFalse(bool);
    }

    @Test
    public void whenInvalidPasswordAndNoLowerCase_ThenError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors("@REN1474");
        Assertions.assertTrue(bool);
        Assertions.assertEquals("Please add a lowercase letter to password", passwordValidator.getErrors().get(0));
    }

    @Test
    public void whenInvalidPasswordAndNoUpperCase_ThenError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors("@ren1474");
        Assertions.assertTrue(bool);
        Assertions.assertEquals("Please add a uppercase letter to password", passwordValidator.getErrors().get(0));
    }

    @Test
    public void whenInvalidPasswordAndNoSpecialCase_ThenError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors("Aren1474");
        Assertions.assertTrue(bool);
        Assertions.assertEquals("Please add a special character to password", passwordValidator.getErrors().get(0));
    }

    @Test
    public void whenInvalidPasswordAndWhiteSpace_ThenError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors(" @Ren1474");
        Assertions.assertTrue(bool);
        Assertions.assertEquals("Please remove the white space", passwordValidator.getErrors().get(0));
    }

    @Test
    public void whenInvalidPasswordAndNoDigit_ThenError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        boolean bool = passwordValidator.hasErrors("@Ren");
        Assertions.assertTrue(bool);
        Assertions.assertEquals("Please add a digit to password", passwordValidator.getErrors().get(0));
    }

    @Test
    public void whenUncheckedPassword_ThenNoError(){
        PasswordValidator passwordValidator = new PasswordValidator();
        String unchecked = "cheeky";
        passwordValidator.setCheckPW(unchecked);
        Assertions.assertEquals(unchecked, passwordValidator.getCheckPW());
    }


}
