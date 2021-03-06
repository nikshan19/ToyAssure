package com.increff.commons.Form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class UploadChannelListingForm {
    @NotNull
    private Long clientId;
    @NotNull
    private Long channelId;
    @NotNull
    @Valid
    private List<ChannelListingForm> channelListings;
}
