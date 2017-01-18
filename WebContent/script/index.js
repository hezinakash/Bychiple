
$(loadIndexPage);

function loadIndexPage()
{
	$.ajax({
		url: "Bychiple/connection/userConnectionCheck",
		type: "POST",
		dataType: 'json',
		success: function(ConnectionData){
			if(ConnectionData.isLoggedIn === true) {
				window.location.href='Home.html';
			}
		},
		error: function() {
        }
	});
};

(function() {

	"use strict";

	// Methods/polyfills.

		// classList | (c) @remy | github.com/remy/polyfills | rem.mit-license.org
			!function(){function t(t){this.el=t;for(var n=t.className.replace(/^\s+|\s+$/g,"").split(/\s+/),i=0;i<n.length;i++)e.call(this,n[i])}function n(t,n,i){Object.defineProperty?Object.defineProperty(t,n,{get:i}):t.__defineGetter__(n,i)}if(!("undefined"==typeof window.Element||"classList"in document.documentElement)){var i=Array.prototype,e=i.push,s=i.splice,o=i.join;t.prototype={add:function(t){this.contains(t)||(e.call(this,t),this.el.className=this.toString())},contains:function(t){return-1!=this.el.className.indexOf(t)},item:function(t){return this[t]||null},remove:function(t){if(this.contains(t)){for(var n=0;n<this.length&&this[n]!=t;n++);s.call(this,n,1),this.el.className=this.toString()}},toString:function(){return o.call(this," ")},toggle:function(t){return this.contains(t)?this.remove(t):this.add(t),this.contains(t)}},window.DOMTokenList=t,n(Element.prototype,"classList",function(){return new t(this)})}}();

		// canUse
			window.canUse=function(p){if(!window._canUse)window._canUse=document.createElement("div");var e=window._canUse.style,up=p.charAt(0).toUpperCase()+p.slice(1);return p in e||"Moz"+up in e||"Webkit"+up in e||"O"+up in e||"ms"+up in e};

		// window.addEventListener
			(function(){if("addEventListener"in window)return;window.addEventListener=function(type,f){window.attachEvent("on"+type,f)}})();

	// Vars.
		var	$body = document.querySelector('body');

	// Disable animations/transitions until everything's loaded.
		$body.classList.add('is-loading');

		window.addEventListener('load', function() {
			window.setTimeout(function() {
				$body.classList.remove('is-loading');
			}, 100);
		});

	// Slideshow Background.
		(function() {

			// Settings.
				var settings = {

					// Images (in the format of 'url': 'alignment').
						images: {
							'css/images/04.jpg': 'center',
							'css/images/05.jpg': 'center',
							'css/images/06.jpg': 'center'
						},

					// Delay.
						delay: 6000

				};

			// Vars.
				var	pos = 0, lastPos = 0,
					$wrapper, $bgs = [], $bg,
					k, v;

			// Create BG wrapper, BGs.
				$wrapper = document.createElement('div');
					$wrapper.id = 'bg';
					$body.appendChild($wrapper);

				for (k in settings.images) {

					// Create BG.
						$bg = document.createElement('div');
							$bg.style.backgroundImage = 'url("' + k + '")';
							$bg.style.backgroundPosition = settings.images[k];
							$wrapper.appendChild($bg);

					// Add it to array.
						$bgs.push($bg);

				}

			// Main loop.
				$bgs[pos].classList.add('visible');
				$bgs[pos].classList.add('top');

				// Bail if we only have a single BG or the client doesn't support transitions.
					if ($bgs.length == 1
					||	!canUse('transition'))
						return;

				window.setInterval(function() {

					lastPos = pos;
					pos++;

					// Wrap to beginning if necessary.
						if (pos >= $bgs.length)
							pos = 0;

					// Swap top images.
						$bgs[lastPos].classList.remove('top');
						$bgs[pos].classList.add('visible');
						$bgs[pos].classList.add('top');

					// Hide last image after a short delay.
						window.setTimeout(function() {
							$bgs[lastPos].classList.remove('visible');
						}, settings.delay / 2);

				}, settings.delay);

		})();

	// Signup Form.
		(function() {

			// Vars.
				var $form = document.querySelectorAll('#signup-form')[0],
					$submit = document.querySelectorAll('#signup-form input[type="submit"]')[0],
					$message;

			// Bail if addEventListener isn't supported.
				if (!('addEventListener' in $form))
					return;

			// Message.
				$message = document.createElement('span');
					$message.classList.add('message');
					$form.appendChild($message);

				$message._show = function(type, text) {

					$message.innerHTML = text;
					$message.classList.add(type);
					$message.classList.add('visible');

					window.setTimeout(function() {
						$message._hide();
					}, 3000);

				};

				$message._hide = function() {
					$message.classList.remove('visible');
				};

			// Events.
			// Note: If you're *not* using AJAX, get rid of this event listener.
				$form.addEventListener('submit', function(event) {

					event.stopPropagation();
					event.preventDefault();

					// Hide message.
						$message._hide();

					// Disable submit.
						$submit.disabled = true;

					// Process form.
					// Note: Doesn't actually do anything yet (other than report back with a "thank you"),
					// but there's enough here to piece together a working AJAX submission call that does.
						window.setTimeout(function() {

							// Reset form.
								//$form.reset();

							// Enable submit.
								$submit.disabled = false;

							// Show message.
								//$message._show('success', 'Thank you!');
								//$message._show('failure', 'Something went wrong. Please try again.');

						}, 750);

				});

		})();

})();

function signInClicked()
{
	$("#msg").empty();
	var goodInput = true;
	
	$("input").each(function(index){
		var str = $(this).val();
		goodInput = (goodInput && isStrNotNullOrEmpty(str));
	});

	if(goodInput) {
		if (validateEmail($("#email").val())) {
			
			//Add loading icon
			$("<div id='loading' class='sk-fading-circle'><div class='sk-circle1 sk-circle'></div><div class='sk-circle2 sk-circle'></div>" +
					"<div class='sk-circle3 sk-circle'></div><div class='sk-circle4 sk-circle'></div>" +
					"<div class='sk-circle5 sk-circle'></div><div class='sk-circle6 sk-circle'></div>" +
					"<div class='sk-circle7 sk-circle'></div><div class='sk-circle8 sk-circle'></div>" +
					"<div class='sk-circle9 sk-circle'></div><div class='sk-circle10 sk-circle'></div>" +
					"<div class='sk-circle11 sk-circle'></div><div class='sk-circle12 sk-circle'></div></div>").insertAfter("#msg");
			$("#submit").addClass("disabled");
			//$("#submit").prop("disabled",true);
			
			doLogin();
		}
		else {
			$("#msg").append("Invalid email address");
		}
	}
	else {	
		$("#msg").append("You must fill all fields");
	}
}

function doLogin()
{
	$.ajax({
		url: "Bychiple/connection/login",
		type: "POST",
		data: {
			email: $("#email").val(),
			pass: $("#password").val()
		},
        dataType: 'json',
        success: function(data) {
        	$("#msg").empty();
        	$("#msg").append(data.reason);
        	$("#loading").remove();	
            if (data.isSucceded === true) {
                window.location = "/Bychiple/Home.html"; 
            }
            else {
            	$("#submit").removeClass("disabled");
            }
//            else {
//            	$("#submit").removeAttr("disabled"); 
//        		$("#submit").prop("disabled",false);
//            }
        },
        error: function() {
        }
	});
}

function validateEmail(email)
{
    var atpos = email.indexOf("@");
    var dotpos = email.lastIndexOf(".");
    if (atpos<1 || dotpos<atpos+2 || dotpos+2>=email.length) {
        return false;
    }
    return true;
}

function isStrNotNullOrEmpty(str)
{
	return ((str !== null) && (str.trim() !== ""));
}