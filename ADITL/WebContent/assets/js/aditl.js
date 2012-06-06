
var aditl = {
    /** action triggered by go button: search on selected date and location **/
    search: function(){
        aditl.getData($('#location').val(), new Date(Date.parse($('#datepicker').val())));
    },
    /** go to previous day **/
    previous: function(){
    	var prevday = new Date(Date.parse($('#datepicker').val()) - 86400000);
    	$('#datepicker').datepicker('setDate', prevday);
        aditl.getData($('#location').val(), prevday);
    },
    /** go to next day **/
    next: function(){
	var nextday = new Date(Date.parse($('#datepicker').val()) + 86400000);
	$('#datepicker').datepicker('setDate', nextday);
        aditl.getData($('#location').val(), nextday);
    },
    lookupState: function(location){
    	var state = "";
		if (location == "Adelaide") {
			state = "SA";
		} else if (location == "Brisbane") {
			state = "QLD";
		} else if (location == "Canberra") {
			state = "ACT";
		} else if (location == "Darwin") {
			state = "NT";
		} else if (location == "Hobart"){
			state = "TAS";
		} else if (location == "Melbourne") {
			state = "VIC";
		} else if (location == "Perth") {
			state = "WA";
		} else {
			state = "NSW";
		}
		return state;
    },
    getData: function(location, date){
    	var formattedDate = date.format("yyyy-mm-dd");
		var state = aditl.lookupState(location);
		document.location.href="/ADITL/#" + escape(location + "+" + formattedDate);
		aditl.displayHeaders(location,state,date);
		
		// dbpedia events 
		$.ajax({
			url: "Events?date=" + formattedDate + "&city=" + location,
			success: function(d){
				aditl.displayEvents(d);
			}
		});
		
		// NAA photos
		$.ajax({
			url: "Photosearch?date=" + formattedDate + "&keyword=" + location,
			complete: function(d){
				//console.log("Got stories",d);
				aditl.displayStories(d.responseText, formattedDate);
			}
		});
		
		// ABS price index
		$.ajax({
			url: "CPI?date=" + formattedDate + "&state=" + state,
			success: function(d){
				aditl.displayPrice(d);
			}
		});
		
		// ABS population - females
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=female",
			success: function(d){
				aditl.displayPopulationFemale(d);
			}
		});
		
		// ABS population - males
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=male",
			success: function(d){
				aditl.displayPopulationMale(d);
			}
		});
		
		// BOM temperature - min
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=min",
			success: function(d){
				aditl.displayTemperatureMin(d);
			}
		});
		
		// BOM temperature - max
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=max",
			success: function(d){
				aditl.displayTemperatureMax(d);
			}
		});
		// BOM rainfall
		$.ajax({
			url: "Rainfall?date=" + formattedDate + "&state=" + state,
			success: function(d){
				aditl.displayRainfall(d);
			}
		});
		
		$.ajax({
			url: "PrimeMinister?date=" + formattedDate,
			success: function(d){
				aditl.displayGovFederal(d);
			}
		});
		/*$.ajax({
		url: "?date=" + formattedDate + "&state=" + state,
		success: function(d){
			aditl.displayGovState(d);
		}
	});*/
		//aditl.displayMusicCharts();
		
		
		
    },
    displayHeaders: function(location, state, date){
		$('.location-display').html(location || "");
		$('.state-display').html(state || "");
		$('.date-display').html($.format.date(date, 'dd MMMM yyyy') || "");
		$('.year-display').html(date.getFullYear());
		
		// display loading icons for the slow loading results
		$('#gov-display').html('');
		$('#events-display').html('<img src="assets/img/ajax-loader.gif">');
		$('#stories-display').html('<img src="assets/img/ajax-loader.gif">');
    },
    
    displayStories: function(data, formattedDate){
    	try {
			if (data){
				var result = "";
				var stories = eval(data);
				//console.log("data was ",data, "stories are",stories);
				// randomly 
				function randomOrder(){
					return (Math.round(Math.random())-0.5);
				}
				if (stories.length <= 1 && formattedDate) {
					// load more general search
					$.ajax({
						url: "Photosearch?date=" + formattedDate,
						complete: function(d){
							//console.log("Got stories",d);
							aditl.displayStories(d.responseText);
						}
					});
					return;
				}
				stories.sort(randomOrder);
				$(stories).each(function(i,d){
					if (i < 18) {
					  var photourl = d.largeImageUrl;
					  if (!photourl) photourl = d.smallImageUrl;
					  var caption = d.title;
					  var link = "#";
					  result +=
						 '<div class="span3 item"><img class="thumbnail" src="' + 
						 photourl +
						 '"><div class="caption"><a href="' +
						 link +
						 '">' +
						 caption
						 + '</a></div></div>';
					}
				});
				$('#stories-display').html(result);
				/*var $container = $('#stories-display');
			    $container.imagesLoaded( function(){
			        $container.masonry({
			         itemSelector : '.item'
			        });
			        
			      });*/
			} else {
				$('#stories-display').html('No data');
			}
    	} catch (ex){
    		$('#stories-display').html('There was a problem loading images: ' + ex.message);
    	}
    },
    displayPopulationFemale: function(data){
		if (data){
			$('#population-display-f').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_035_woman.png"> &nbsp; <div class="dataval">' + aditl.formatNumber(data) + '</div>');
		} else {
		    $('#population-display-f').html('--');
		}
    },
    displayPopulationMale: function(data){
		if (data){
			$('#population-display-m').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_034_old_man.png"> &nbsp; <div class="dataval">' + aditl.formatNumber(data) + '</div>');
		} else {
		    $('#population-display-m').html('--');
		}
    },
    displayTemperatureMin: function(data){
		if (data){
			$('#weather-display-min').html('<div class="dataval">' + data + ' &#176;C</div>');
		} else {
		    $('#weather-display-min').html('--');
		}
    }, 
    displayTemperatureMax: function(data){
		if (data){
			$('#weather-display-max').html('<div class="dataval"> &ndash; ' + data + ' &#176;C</div>');
		} else {
		    $('#weather-display-max').html('--');
		}
    },
    displayRainfall: function(data){
    	var rainfall = parseInt(data);
    	var icon = "<img class='dataicon' src='assets/img/iconic-black/sun_fill_24x24.png'>";
    	if (rainfall >= 1){
    		icon = "<img class='dataicon' src='assets/img/iconic-black/rain_24x21.png'>";
    	}
		if (data){
			$('#weather-display-rain').html(icon + '<div class="dataval">&nbsp;' + data + ' mm</div>');
		} else {
		    $('#weather-display-rain').html('--');
		}
    },
    displayMusicCharts: function(data){
		if (data){
//		     '<img src="assets/img/glyphicons/glyphicons_017_music.png">'
		} else {
		    $('#music-display').html('--');
		}
	
    },
    displayEvents: function(data){
    	try {
			if (data){
				var result = "";
				var events = eval(data);
				if (events.results && events.results.bindings && events.results.bindings.length > 0) {
					var done = {};
					$(events.results.bindings).each(function(i,val){
						var link = "";
						var name = "";
						var eventlabel = " was born";
						if (val.page){
							link = val.page.value;
						}
						if (val.name){
							name = val.name.value;
						}
						if (val.placelabel){
							eventlabel += " in " + val.placelabel.value;
						}
						if (!done[name]){
							result += "<div style='margin-top:8px'><img class='dataicon' src='assets/img/glyphicons/glyphicons_045_calendar.png'> &nbsp;" + (link? "<a href='" + link + "'>" + name + "</a>" : name) + eventlabel + "</div>";
						}
						done[name] = true;
					});
					$('#events-display').html(result);
				} else {
					$('#events-display').html('<span style="font-size:smaller">(no events found)</span>');
				}
			} else {
				$('#events-display').html('--');
			}
    	} catch (ex){
    		$('#events-display').html('--');
    	}
    },
    displayGovFederal: function(data){
		if (data){
			var pmdata = eval(data);
			if (pmdata && pmdata.ministry){
				//console.log(pmdata);
				var pm = pmdata.ministry || "";
				var party = pmdata.party || "";
				
				$('#gov-display').html("<img class='dataicon' src='assets/img/glyphicons/glyphicons_263_bank.png'>  <span class='dataval'>" + pm + "</span><br/>" +
						"<span style='font-size:small' class='dataval'>" + party + "</span>");
			} else {
				$('#gov-display').html('--');	
			}
		} else {
			$('#gov-display').html('--');
		}
    },
    displayGovState: function(data){
		if (data){
		} else {
		    $('#gov-display').html('--');
		}
    },
    displayPrice: function(data){
		if (data){
			//  multiply value to get price of loaf of bread - $2.50 in 2012
			var f = parseFloat(data) * 2.5;
			f = f.toFixed(2);
			$('#price-display').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_227_usd.png"> <div class="dataval">' + f + '</div>');
		} else {
		    $('#price-display').html('--');
		}
    },
   formatNumber: function(num) {
    	if (num){
	    	var str = num.split('.');
	    	var str1 = str[0]; // remove decimal places
	    	// add commas
	    	var rgx = /(\d+)(\d{3})/;
	    	while (rgx.test(str1)) {
	    		str1 = str1.replace(rgx, '$1' + ',' + '$2');
	    	}
	    	return str1;
    	}
    }
}