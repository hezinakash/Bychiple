
var hasChanges = false;

$(loadSettingsPage);

function loadSettingsPage() 
{
	$("#sumbitMsg").text("");
	
	setEvents();  
	initSettingsFields();
}

function initSettingsFields()
{
	// Get the configuration from server
	$.ajax({
		url : "Bychiple/settings/getSettings",
		type : "POST",
		dataType : 'json',
		async: false,
		timeout: 3000,
		success : function(settingsList) { 

			var smsFlag = settingsList[0];
			var electricFlag = settingsList[1];
			var weight = settingsList[2] + " Kg";			
			
			$("#smsFlag").prop('checked', smsFlag);
			$("#electricFlag").prop('checked', electricFlag);
			$(".range-slider__range").attr('value', weight);
			$(".range-slider__value").text(weight);
			
			if (smsFlag !== undefined && electricFlag !== undefined) {
				// get user chips list
				//getUserChips(smsFlag, electricFlag);
				getUserChips();
			}
		},
		error : function() {
		}
	});
}

function setEvents()
{
	$("#smsFlag").click(setHasChanges);
	$("#electricFlag").click(onElectricFlagClicked);
	$(".range-slider__range").click(setHasChanges);
	
	$(".range-slider__range").on('input', function(){
		$(".range-slider__value").html(this.value + " Kg");
	});
}

function setHasChanges()
{
	hasChanges = true;
}

function onElectricFlagClicked()
{
	var isElectric = $("#electricFlag").prop('checked');
	if (isElectric === true)
	{
		$("#weightDiv").addClass("disabled");
	}
	else
	{
		$("#weightDiv").removeClass("disabled");
	}
	
	setHasChanges();
}

function getUserChips() 
{	
	$.ajax({
		url : "Bychiple/userChipsManager/getUserChipsList",
		type : "POST",
		dataType : 'json',
		success : function(userChips) {
			if (userChips !== undefined)
				{
					//$("#smsFlag").prop('checked', smsFlag);          
					//$("#electricFlag").prop('checked', electricFlag);
					addChipsListToCombobox(userChips);
					onSelectChip();
					onAddChipBtnClick();
					}
				},
				error : function() {
				}
			});
}

function addChipsListToCombobox(userChips)
{
	// add chips to html menuDown
	$("#menuDown").empty();
	$.each(userChips.userChipsList, function(i, userChip)
		{
			$("#menuDown").append("<li><a href='#' id='" + userChip.value+ "'>"
	   		   				      + userChip.text + "</a></li>");

			if (userChip.value === userChips.currChipID)
			{
				$("#dropdownBtn").html(userChip.text + ' <span class="caret"></span>');
				$("#dropdownBtn").val(userChip.value);
			}
		});
}

function onSelectChip() 
{
	$(".dropdown-menu li a").click(
			function() {
				var selText = $(this).text();
				$(this).parents('.btn-group').find('.dropdown-toggle').html(
						selText + ' <span class="caret"></span>');

				$("#dropdownBtn").val($(this).attr('id'));
				setHasChanges();
			});
}

function onAddChipBtnClick() 
{
	$("#addChipBtn").click(function() {
		// empty addChip form
		$("#log_chipID").val("");
		$("#log_nickname").val("");
		$("#successMsg").text("");
		$("#errorMsg").text("");

		// change value + / -
		var valAddBtn = $("#addChipBtn").text();
		if (valAddBtn === "+")
			$("#addChipBtn").text("-");
		else
			$("#addChipBtn").text("+");
	});
}

function onChangeChipDetails() 
{
	// empty addChip form
	$("#errorMsg").text("");
}

function addNewChipClicked() 
{
	$("#errorMsg").text("");
	var chipID = $("#log_chipID").val();
	var chipNickname = $("#log_nickname").val();

	if (isStrNotNullOrEmpty(chipID) && isStrNotNullOrEmpty(chipNickname)) {
		if (isEnglishLetter(chipID) && isEnglishLetter(chipNickname)) {

			addChipToDataBase();
		} else {
			$("#errorMsg")
					.append(
							"All fields must be written with english letters or digits");
		}
	} else {
		$("#errorMsg").append("You must fill all fields");
	}
}

function addChipToDataBase() 
{
	$.ajax({
		url : "Bychiple/userChipsManager/addChip",
		type : "POST",
		data : {
			chipID : $("#log_chipID").val(),
			nickname : $("#log_nickname").val()
		},
		dataType : 'json',
		success : function(data) {

			if (data.isSucceded === true) {
				// addChipsOfUserToCombobox();
				$("#menuDown").append(
						"<li><a href='#' id='" + $("#log_chipID").val() + "'>"
								+ $("#log_nickname").val() + "</a></li>");

				onSelectChip();
				$("#addChipBtn").click(); // close the "add chip" form 
				$("#successMsg").append(data.reason);
			} else {
				$("#errorMsg").append(data.reason);
			}
		},
		error : function() {
		}
	});
}

function onSubmitBtnClicked() 
{
	$("#successMsg").text("");
	if ($("#errorMsg").text() !== "") 
	{
		$("#sumbitMsg").text("Cannot save changes");
		$("#sumbitMsg").css('color', 'red');
		// clear submit msg after 3 sec
		setTimeout(function() {

			$("#sumbitMsg").text("");
		}, 5000);
	} 
	else 
	{
		if(hasChanges)
		{
			saveChanges();
			hasChanges = false;
		}
	}
}

function saveChanges()
{
	$.ajax({
		url : "Bychiple/settings/saveChanges",
		type : "POST",
		dataType : 'json',
		data : {
				selectedChipID : $("#dropdownBtn").val(),          // choosen chip
				smsFlag : $("#smsFlag").prop('checked'),		   // updated sms flag
				electricFlag : $("#electricFlag").prop('checked'), // updated electric flag
				weight: $(".range-slider__range").val()			   // updated weight
		    	},
		success : function(isSucess) 
		{
			if (isSucess === true)
			{
				$("#sumbitMsg").text("Changes saved successfully");
				$("#sumbitMsg").css('color', 'green');
			}
			else
			{
				$("#sumbitMsg").text("Failed to save changes");
				$("#sumbitMsg").css('color', 'red');
			}
			
			// clear submit msg after 3 sec
			setTimeout(function() {$("#sumbitMsg").text("");}, 5000);
		},
		error : function() {}	
	});
}

function onBackBtnClicked() 
{
	window.location.href = 'Home.html';
}

// ////--------------------------------
// ////- Validation -
// ////--------------------------------

function isStrNotNullOrEmpty(str) 
{
	return ((str !== null) && (str.trim() !== ""));
}

function isEnglishLetter(val) 
{
	var result = /[^\x00-\x7F]+/.test(val);
	return !result;
}