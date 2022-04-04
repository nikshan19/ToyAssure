package com.increff.commons.Data;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name="items")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceData {

    @XmlElement(name="orderId")
    private Long orderId;
    @XmlElement(name="channelOrderId")
    private String channelOrderId;
    @XmlElement(name="channelName")
    private String channelName;
    @XmlElement(name="clientName")
    private String clientName;
    @XmlElement(name="customerName")
    private String customerName;
    @XmlElement(name="dateTime")
    private String GeneratedDateTime;
    @XmlElement(name="total")
    private Double total;
    @XmlElement(name="item")
    private List<InvoiceProductData> invoiceProductDataList;
}
