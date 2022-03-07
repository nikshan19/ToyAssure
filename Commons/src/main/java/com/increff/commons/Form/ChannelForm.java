package com.increff.commons.Form;

import com.increff.commons.Constants.Invoice.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelForm {
    @NotNull
    private String channelName = "Internal" ;
    @NotNull
    private InvoiceType invoiceType = InvoiceType.SELF;
}