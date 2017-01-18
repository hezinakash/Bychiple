addEventListener("load", function(){ setTimeout(hideURLbar, 0); }, false); 

function hideURLbar()
{ 
	window.scrollTo(0,1); 
}

function signUpClicked()
{
	var allFilled = true;
	
	$("#msg").empty();
	
	$("input").each(function(index){
		var str = $(this).val();
		allFilled = allFilled && (str !== null) && (str.trim() !== "");
	});
	
	if(allFilled) {
		if (isAllInputInEnglish()) {
			manageSignUp();
		}
		else {
			$("#msg").append("Not all fields written in english");
		}
	}
	else {
		$("#msg").append("Not all fields were filled");
	}
}

function isAllInputInEnglish()
{	
	var result = true;
	
	$("input").each(function(index){
		var str = $(this).val();
		result = result && isEnglishLetter(str);
	});
	
	return result;
}

function isEnglishLetter(val)
{
	var result = /[^\x00-\x7F]+/.test(val);
	return !result;
}

function manageSignUp()
{
	var userEmail = $("#log_email").val();
	var phoneNum = $("#log_phone").val();
	var isPhonenum = /^\d+$/.test(phoneNum);
	
	if (!isPhonenum) {
		$("#msg").append("Phone number must contain only digits");
	}
	else if (!validateMail(userEmail)) {
		$("#msg").append("Invalid email address");
	}
	else {
		
		//Add loading.. NEW 31.8
		$("<div id='loading' class='sk-fading-circle'><div class='sk-circle1 sk-circle'></div><div class='sk-circle2 sk-circle'></div>" +
				"<div class='sk-circle3 sk-circle'></div><div class='sk-circle4 sk-circle'></div>" +
				"<div class='sk-circle5 sk-circle'></div><div class='sk-circle6 sk-circle'></div>" +
				"<div class='sk-circle7 sk-circle'></div><div class='sk-circle8 sk-circle'></div>" +
				"<div class='sk-circle9 sk-circle'></div><div class='sk-circle10 sk-circle'></div>" +
				"<div class='sk-circle11 sk-circle'></div><div class='sk-circle12 sk-circle'></div></div>").insertAfter("#msg");
		$("#submit").removeAttr("disabled");
		$("#submit").prop("disabled",true);
		
		doTheSignUp();
	}
}

function doTheSignUp()
{
	$.ajax({
		url: "Bychiple/connection/signUp",
		type: "POST",
		data: {
			chipID: $("#log_chipID").val(),
			nickname: $("#log_nickname").val(),
			fname: $("#log_fname").val(),
			lname: $("#log_lname").val(),
			phone: $("#log_phone").val(),
			email: $("#log_email").val(),
			pass: $("#log_pass").val()
		},
        dataType: 'json',
        success: function(data) {
        	$("#msg").empty();
        	$("#msg").append(data.reason);
        	$("#loading").remove();			//NEW 31.8
        	console.log("in success");
            if (data.isSucceded === true) {
            	 window.location.href = "/Bychiple/Home.html";
            }
            else {
            	$("#submit").removeAttr("disabled"); //NEW 31.8
        		$("#submit").prop("disabled",false);
            }
        },
        error: function() {
        	console.log("error sign up");
        }
	});
}

function validateMail(email)
{
    var atpos = email.indexOf("@");
    var dotpos = email.lastIndexOf(".");
    if (atpos<1 || dotpos<atpos+2 || dotpos+2>=email.length) {
        return false;
    }
    return true;
}
