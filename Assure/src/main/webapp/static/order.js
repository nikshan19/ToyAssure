
function getOrderUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getPartyUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/parties";
}

function getChannelUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/channels";
}

function getProductUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function getInvoiceUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/invoice";
}

function csv2Json(){
const uploadconfirm = document.getElementById("process-data").
addEventListener('click', () => {
Papa.parse(document.getElementById('employeeFile').files[0],
{
    download:true,
    header:true,
    skipEmptyLines: true,
    complete: function(results){
            if(results.data.length>5000){
                showError("CSV rows must be less than 5000");
                return false;
            }

            cId = $("#inputClientId").val();
            cuId = $("#inputCustomerId").val();
            cOID = $("#inputChannelOrderId").val();
            const data = {clientId:cId, customerId:cuId, channelOrderId:cOID, formList:results.data};
            var jsonData = JSON.stringify(data);
            addEmployee(jsonData);
    }
});
});

}
function addEmployee(jsonData) {
	//Set the values to update
	var url = getOrderUrl()+"/upload";

	$.ajax({
		url: url,
		type: 'POST',
		data: jsonData,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(data, textStatus, xhr) {
			$('#upload-order-modal').modal('toggle');
			$("#upload-order-form").trigger("reset");
			showSuccess("Success");
		},
		error: function(data) {

		    var response = JSON.parse(data.responseText);
            var msg = response.message;
            showError(msg);
		}
	});

	return false;
}

function searchOrder(event) {
	//Set the values to update
	var $form = $("#order-search-form");
	var json = toJson($form);
	var url = getOrderUrl()+"/search";

	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(data) {
			showSuccess("Success");
            displayEmployeeList(data);
		},
		error: function(data, textStatus, xhr) {
			var response = JSON.parse(data.responseText);
            var msg = response.message;
            showError(msg);
		}
	});

	return false;
}

function searchOrder2(event) {
	var id = $("#inputOrderIdSearch").val();
	var url = getOrderUrl()+"/search/"+id;

	$.ajax({
		url: url,
		type: 'GET',
		success: function(data) {
		    console.log(data);
			showSuccess("Success");
            displayEmployeeList(data);
		},
		error: function(data, textStatus, xhr) {
		     var response = JSON.parse(data.responseText);
		     var msg = response.message;
			 showError(msg);
		}
	});

	return false;
}


function displayEmployeeList(data) {
    if(data.length>5){
        document.getElementById('footer').style.display = "";
        }
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for (var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button" class="btn btn-outline-info border-0" data-toggle="tooltip" title="More Info" onclick="getOrderItems(' + e.id + ')">More Info</button>';
		if(e.orderStatus === "CREATED"){
		    buttonHtml+= ' <button type="button" class="btn btn-outline-secondary border-0" data-toggle="tooltip" title="Allocate" onclick="allocateOrder(' + e.id + ')">Allocate</button>';
		}
		else if(e.orderStatus === "ALLOCATED"){
		    buttonHtml+= ' <button type="button" class="btn btn-outline-primary border-0" data-toggle="tooltip" title="Generate Invoice" onclick="getInvoice(' + e.id + ')">Generate Invoice</button>';
		}
		else{
		    buttonHtml+= ' <button type="button" class="btn btn-outline-primary border-0" data-toggle="tooltip" title="Get Invoice" onclick="getInvoice(' + e.id + ')">Download Invoice</button>';
		}

		var row = '<tr>'
			+ '<td>' + e.clientId + '</td>'
			+ '<td>' + e.customerId + '</td>'
			+ '<td>' + e.channelId + '</td>'
			+ '<td>' + e.channelOrderId + '</td>'
			+ '<td>' + e.orderStatus + '</td>'
			+ '<td>' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);

	}
}

function getOrderItems(id){
    	var url = getOrderUrl()+"/order-items/"+id;
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	        displayOrderItemList(data);
    	   		     //...
    	   },
    	   error: function(data){
    	   		 var response = JSON.parse(data.responseText);
                 var msg = response.message;
                 showError(msg);
    	   }
    	});
    }


function getInvoice(id){
    var url = getInvoiceUrl()+"?orderId="+id;
        	$.ajax({
        	   url: url,
        	   type: 'GET',
        	   success: function(data) {
        	        showPdf(data);
        	        searchOrder();

        	   },
        	   error: function(data){
        	   		 var response = JSON.parse(data.responseText);
                     var msg = response.message;
                     showError(msg);
        	   }
        	});
        }


function displayOrderItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];

         var row = '<tr>'
                         + '<td>' + e.clientSkuId + '</td>'
                          +'<td>' + e.orderedQty + '</td>'
                          + '<td>' + e.allocatedQty + '</td>'
                          + '<td>' + e.fulfilledQty + '</td>'
                           + '<td>' + e.sellingPricePerUnit + '</td>'
                            + '</tr>';
                            $tbody.append(row);
}
	var span = document.getElementById("spanB");
    span.innerHTML = "Order ID: " + data[0].orderId;
	$('#order-item-modal').modal('toggle');
}




function allocateOrder(id){
        var url = getOrderUrl()+"/allocate?id="+id;
    	$.ajax({
    	   url: url,
    	   type: 'PUT',
    	   success: function(data) {
    	        searchOrder();
    	   		     //...
    	   },
    	   error: function(data){
    	   		var response = JSON.parse(data.responseText);
                var msg = response.message;
                showError(msg);
    	   }
    	});
    }



//HELPER METHOD
function toJson($form) {
	var serialized = $form.serializeArray();
	var s = '';
	var data = {};
	for (s in serialized) {
		data[serialized[s]['name']] = serialized[s]['value']
	}
	var json = JSON.stringify(data);
	//console.log(json);
	return json;
}

function showError(msg) {

	$('#EpicToast').html('<div class="d-flex">'
		+ '<div class="toast-body">'
		+ '' + msg + ''
		+ ' </div>'
		+ '<button type="button" class="close btn-close btn-close-white me-2 m-auto" data-dismiss="toast" aria-label="Close"></button>'
		+ '</div>'

	);


	var option = {
		animation: true,
		autohide: false
	};
	var t = document.getElementById("EpicToast");
	var tElement = new bootstrap.Toast(t, option);
	tElement.show();

}

function showSuccess(msg) {

	$('#EpicToast1').html('<div class="d-flex">'
		+ '<div class="toast-body ">'
		+ '' + msg + ''
		+ ' </div>'
		+ '<button type="button" class="close btn-close btn-close-white me-2 m-auto" data-dismiss="toast" aria-label="Close"></button>'
		+ '</div>'

	);


	var option = {
		animation: true,
		delay: 2000
	};
	var t = document.getElementById("EpicToast1");
	var tElement = new bootstrap.Toast(t, option);
	tElement.show();

}

function showPdf(data) {
    var fileName = 'Invoice_order_id_'+data.orderId;
    var contentType = "application/pdf";
     const blob = b64toBlob(data.b64Data, contentType);
    const blobUrl = URL.createObjectURL(blob);

                var isIE = false || !!document.documentMode;
                if (isIE) {
                    window.navigator.msSaveBlob(blob, fileName);
                } else {
                    var url = window.URL || window.webkitURL;
                    link = blobUrl;
                    var a = document.createElement("a");
                    a.setAttribute("download", fileName);
                    a.setAttribute("href", link);
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                }
  }

const b64toBlob = (b64Data, contentType = '', sliceSize = 512) => {
    const byteCharacters = atob(b64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize)
       {
        const slice = byteCharacters.slice(offset, offset + sliceSize);

        const byteNumbers = new Array(slice.length);
        for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        const byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }

    const blob = new Blob(byteArrays, { type: contentType });
    return blob;
}

function openCity(evt, cityName) {
  // Declare all variables
  console.log(evt);

  var i, tabcontent, tablinks;
  tabcontent = document.getElementsByClassName("tabcontent");
  tablinks = document.getElementsByClassName("tablinks");

  // Get all elements with class="tabcontent" and hide them

  for (i = 0; i < tabcontent.length; i++) {
    tabcontent[i].style.display = "none";
  }

  // Get all elements with class="tablinks" and remove the class "active"
  tablinks = document.getElementsByClassName("tablinks");
  for (i = 0; i < tablinks.length; i++) {
    tablinks[i].className = tablinks[i].className.replace(" active", "");
  }

  // Show the current tab, and add an "active" class to the button that opened the tab
  document.getElementById(cityName).style.display = "block";
  evt.currentTarget.className += " active";
}



function validateOrderIdField(){
    var orderId = document.getElementById("inputOrderIdSearch").value;
    if(orderId.length==0){
        showError("Order Id must not be empty");
        return false;
    }
    searchOrder2();
}
var globalData;
function init(){
    	csv2Json();
    	globalData = [];
    	$('#search-orders').click(searchOrder);
    	$('#search-orders2').click(validateOrderIdField);
        $('#change').click(change);
}
$(document).ready(init);
$(document).ready(show);
$(document).ready(dropdown1);
$(document).ready(dropdown2);
$(document).ready(dropdown3);
$(document).ready(openCity);

function show(){
	$("#datepicker1").datepicker({
  format: 'yyyy-mm-dd',
  maxDate: '0' // change format here

  });
  $("#datepicker2").datepicker({
  format: 'yyyy-mm-dd',
  maxDate: '0'// change format here

  });
}

function dropdown1(){

	var url = getPartyUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
		var select = document.getElementById('inputClientId');
		for(var i in data){
		var e = data[i];
		if(e.partyType === 'CLIENT'){
		var opt = document.createElement('option');
		opt.value = e.partyId;
		opt.innerHTML = e.partyName;
		select.appendChild(opt);
		}
	}

	   },
	   error: function(){
	   		showError("An error has occurred");
	   }
	});


}

function dropdown2(){

	var url = getPartyUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
		var select = document.getElementById('inputCustomerId');
		for(var i in data){
		var e = data[i];
		if(e.partyType === 'CUSTOMER'){
		var opt = document.createElement('option');
		opt.value = e.partyId;
		opt.innerHTML = e.partyName;
		select.appendChild(opt);
		}
	}

	   },
	   error: function(){
	   		showError("An error has occurred");
	   }
	});


}

function change() {

          if($('#searchType').val() == 1){
          document.getElementById("select-by-orderId").style.display="block";
          document.getElementById("order-search-form").style.display="none";
          }
          if($('#searchType').val() == 2){
          document.getElementById("select-by-orderId").style.display="none";
          document.getElementById("order-search-form").style.display="block";
          }

 }

function DownloadFile(fileUrl) {
            //Set the File URL.

            //Create XMLHTTP Request.
            const fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            var req = new XMLHttpRequest();
            req.open("GET", fileUrl, true);
            req.responseType = "blob";
            req.onload = function () {
                //Convert the Byte Data to BLOB object.
                var blob = new Blob([req.response], { type: "application/pdf" });

                //Check the Browser type and download the File.
                var isIE = false || !!document.documentMode;
                if (isIE) {
                    window.navigator.msSaveBlob(blob, fileName);
                } else {
                    var url = window.URL || window.webkitURL;
                    link = URL.createObjectURL(blob);
                    var a = document.createElement("a");
                    a.setAttribute("download", fileName);
                    a.setAttribute("href", link);
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                }
            };
            req.send();
        };


function dropdown3(){

	var url = getChannelUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
		var select = document.getElementById('inputChannelId');
		for(var i in data){
		var e = data[i];
		var opt = document.createElement('option');
		opt.value = e.id;
		opt.innerHTML = e.channelName;
		select.appendChild(opt);
	}
	var opt = document.createElement('option');
    		opt.value = null;
    		opt.innerHTML = "None";
    		select.appendChild(opt);


},
	   error: function(){
	   		showError("An error has occurred");
	   }
	});


}

