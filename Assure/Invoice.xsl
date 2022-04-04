<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- Attribute used for table border -->
	<xsl:attribute-set name="tableBorder">
		<xsl:attribute name="border">solid 0.1mm black</xsl:attribute>
	</xsl:attribute-set>
	<xsl:template match="items">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21.0cm" margin="1cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<fo:table>
							<fo:table-column column-width="100%" />

							<fo:table-body>
								<fo:table-row font-size="18pt" line-height="30px"
									background-color="gray" color="black">

									<fo:table-cell>
										<fo:block text-align="center" padding-right="6pt">
											Invoice
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>

					</fo:block>
					<fo:table>
						<fo:table-column column-width="50%"/>
						<fo:table-column column-width="50%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block padding-top="15pt" padding-bottom="2pt" font-size="10pt"
										font-family="Helvetica" color="black" font-weight="bold"
										space-after="5mm">
										Order Id:
										<xsl:value-of select="orderId" />
									</fo:block>


								</fo:table-cell>

								<fo:table-cell>
									<fo:block padding-top="15pt" padding-bottom="2pt" font-size="10pt" font-family="Helvetica"
										color="black" font-weight="bold" space-after="5mm" text-align="right">
										Generation Time:
										<xsl:value-of select="dateTime" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row >
							    <fo:table-cell>
                                    <fo:block padding-top="2pt" padding-bottom="2pt" font-size="10pt"
                                        font-family="Helvetica" color="black" font-weight="bold"
                                        space-after="5mm">
                                        Customer:
                                        <xsl:value-of select="customerName" />
                                    </fo:block>

                            </fo:table-cell>

                             <fo:table-cell>
                                 <fo:block padding-top="2pt" padding-bottom="2pt" font-size="10pt"
                                        font-family="Helvetica" color="black" font-weight="bold"
                                        space-after="5mm" text-align="right">
                                        Client:
                                    <xsl:value-of select="clientName" />
                                </fo:block>

                             </fo:table-cell>

							</fo:table-row>

							<fo:table-row>
							<fo:table-cell>
                                <fo:block padding-top="2pt" padding-bottom="10pt" font-size="10pt"
                                font-family="Helvetica" color="black" font-weight="bold"
                                space-after="5mm">
                                Channel:
                                <xsl:value-of select="channelName" />
                                </fo:block>

                                </fo:table-cell>

                                <fo:table-cell>
                                   <fo:block padding-top="2pt" padding-bottom="10pt" font-size="10pt"
                                      font-family="Helvetica" color="black" font-weight="bold"
                                       space-after="5mm" text-align="right">
                                       Channel Order ID:
                                       <xsl:value-of select="channelOrderId" />
                                   </fo:block>

                                </fo:table-cell>

							</fo:table-row>


						</fo:table-body>
					</fo:table>


					<fo:block font-size="10pt">
						<fo:table table-layout="fixed" width="80%"
							border-collapse="separate" margin-left="15pt">
							<!-- <fo:table-column column-width="2cm" />-->
							<fo:table-column column-width="4cm" />
							<fo:table-column column-width="4cm" />
							<fo:table-column column-width="4cm" />
							<fo:table-column column-width="3cm" />
							<fo:table-column column-width="3cm" />
							<fo:table-header>
								<!-- <fo:table-cell
									xsl:use-attribute-sets="tableBorder">
									<fo:block font-weight="bold" text-align="center">Id</fo:block>
								</fo:table-cell>-->
								<fo:table-cell
									xsl:use-attribute-sets="tableBorder">
									<fo:block font-weight="bold" text-align="center">Name</fo:block>
								</fo:table-cell>
								<fo:table-cell
                                	xsl:use-attribute-sets="tableBorder">
                                	<fo:block font-weight="bold" text-align="center">Client Sku ID</fo:block>
                                	</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="tableBorder">
									<fo:block font-weight="bold" text-align="center">Ordered Quantity</fo:block>
								</fo:table-cell>
									<fo:table-cell
									xsl:use-attribute-sets="tableBorder">
									<fo:block font-weight="bold" text-align="right">Selling Price</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="tableBorder">
									<fo:block font-weight="bold" text-align="right">Amount</fo:block>
								</fo:table-cell>
							</fo:table-header>
							<fo:table-body>
								<xsl:apply-templates select="item" />
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block font-size="10pt" font-family="Helvetica"
						color="black" font-weight="bold" padding-top="2pt" padding-right="15pt" text-align="right" space-after="5mm">
						Total: Rs.
						<xsl:value-of select="format-number(total, '#.00')" />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="item">
		<fo:table-row>
			<!-- <fo:table-cell xsl:use-attribute-sets="tableBorder">
				<fo:block text-align="center">
					<xsl:value-of select="id" />
				</fo:block>
			</fo:table-cell>-->

			<fo:table-cell xsl:use-attribute-sets="tableBorder">
				<fo:block text-align="center">
					<xsl:value-of select="productName" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tableBorder">
            	<fo:block text-align="center">
            		<xsl:value-of select="clientSkuId" />
            	</fo:block>
            </fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tableBorder">
					<fo:block text-align="center">
					<xsl:value-of select="quantity" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tableBorder">

				<fo:block text-align="right">
					<xsl:value-of select="format-number(sellingPricePerUnit, '#.00')" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="tableBorder">

				<fo:block text-align="right">
					<xsl:value-of select="format-number(sellingPricePerUnit * quantity, '#.00')" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
</xsl:stylesheet>