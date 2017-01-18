
//********************************
//*          On Load             *
//********************************

$(loadMsgBoxPage);

function loadMsgBoxPage()
{
	$('#set').empty();
	
	$.ajax({
        url: "Bychiple/messageBoxManager/loadMessageBox",
        type: "POST",
        dataType: 'json',
        success: function(msgList) {
        	$.each(msgList, function(i, msg){
        		var collapseMsg = buildMsg(msg);
                $("#set").append(collapseMsg).collapsibleset('refresh');
        	});
        	
        	$.each(msgList, function(i, msg){
        		var date = new Date(msg.dateAndTime);
        		var datestring = date.getDate()  + "/" + (date.getMonth()+1) + "/" + date.getFullYear();
        		$("#" + msg.messageId + " a").append('<label style="float: right;">' + datestring + '</label>');
        		
        		if (window.matchMedia('(max-width: 600px)').matches) {
        			$("#" + msg.messageId + " a").css("padding-top", "20px");
        		}
        		else {
        			$("#" + msg.messageId + " a").css("padding-top", "19px");
        		}
        		
        		if (msg.isRead) {
        			$("#" + msg.messageId).addClass('read');
        			$("#" + msg.messageId + " a").css("font-weight", "normal");
        			$("#" + msg.messageId + " a label").css("font-weight", "normal");
        		}
        		else {
        			$("#" + msg.messageId + " a label").css("font-weight", "bold");
        		}
                $("#set").collapsibleset('refresh');
        	});
        },
        error: function() {
            console.log("Failed to show messages list");
        }
    });
}

function buildMsg(msg)
{
	var collapseMsg = "<div data-role='collapsible'><h3 id='" + msg.messageId + "'";
	
	if (!msg.isRead) {
		collapseMsg += " onclick='unreadMsgClicked(this)'";
	}

	collapseMsg +=  ">" + msg.title + "</h3><p><br/>" +
	  			   msg.content + "<br/></p></div>";
	
	return collapseMsg;
}

function unreadMsgClicked(elem)
{
	if (!($(elem).hasClass("read")))
	{
		var msgID = $(elem).attr('id');
		$(elem).addClass('read');
		$("#" + msgID + " a").css("font-weight", "normal");
		$("#" + msgID + " a label").css("font-weight", "normal");
		updateReadMsgInDB(msgID);
	}
}

function updateReadMsgInDB(msgID)
{
	$.ajax({
        url: "Bychiple/messageBoxManager/updateReadMsg",
        type: "POST",
        data: {msgId: msgID},
        dataType: 'json',
        success: function() {
        },
        error: function() {
            console.log("Failed to update read message");
        }
    });
}

function backBtnClicked()
{
	window.location.href = "/Bychiple/Home.html";
}
