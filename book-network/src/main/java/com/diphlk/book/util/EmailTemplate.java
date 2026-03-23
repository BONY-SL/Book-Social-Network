package com.diphlk.book.util;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACCOUNT_ACTIVATION("activate_account");


    private final String templateName;

    EmailTemplate(String templateName) {
        this.templateName = templateName;
    }

}
