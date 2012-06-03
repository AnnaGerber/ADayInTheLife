
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
		aditl.displayHeaders(location,state,date);
		
		// dbpedia events 
		$.ajax({
			url: "Events?date=" + formattedDate + "&city=" + location,
			success: function(d){
				aditl.displayEvents(d);
			}
		});
		
		// ABS price index
		$.ajax({
			url: "CPI?date=" + formattedDate + "&state=" + state,
			success: function(d){
				aditl.displayPrice(d);
			}
		});
		
		// ABS population
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=female",
			success: function(d){
				aditl.displayPopulationFemale(d);
			}
		});
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=male",
			success: function(d){
				aditl.displayPopulationMale(d);
			}
		});
		
		// BOM temperature
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=min",
			success: function(d){
				aditl.displayTemperatureMin(d);
			}
		});
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=max",
			success: function(d){
				aditl.displayTemperatureMax(d);
			}
		});
		/*$.ajax({
			url: "?date=" + formattedDate,
			success: function(d){
				aditl.displayGovFederal(d);
			}
		});*/
		/*$.ajax({
		url: "?date=" + formattedDate + "&state=" + state,
		success: function(d){
			aditl.displayGovState(d);
		}
	});*/
		aditl.displayMusicCharts();
		
		
		
    },
    displayHeaders: function(location, state, date){
		$('.location-display').html(location || "");
		$('.state-display').html(state || "");
		$('.date-display').html($.format.date(date, 'dd MMMM yyyy') || "");
		$('.year-display').html(date.getFullYear());
		
    },
    displayPhotos: function(data){
	/*if (data) {
		data.each(function(d){
	
	  var photourl = d.large_image_url;
	  var caption = d.title;
	  var link = d.stories_url;
	  $('#stories-display')
	   .html(
		 '<div class="span3 item"><img class="thumbnail" src="' + 
		 photourl +
		 '"><div class="caption"><a href="' +
		 link +
		 '">' +
		 caption
		 + '</a></div></div>'
	   );
       });
	 }*/
    },
    displayPopulationFemale: function(data){
		if (data){
			$('#population-display-f').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_035_woman.png"> <div class="dataval">' + aditl.formatNumber(data) + '</div>');
		} else {
		    $('#population-display-f').html('--');
		}
    },
    displayPopulationMale: function(data){
		if (data){
			$('#population-display-m').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_034_old_man.png"> <div class="dataval">' + aditl.formatNumber(data) + '</div>');
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
    displayMusicCharts: function(data){
		if (data){
		} else {
		    $('#music-display').html('--');
		}
	// assume data > song > title
	//	$('#musicCharts').html(
      //     '<img src="assets/img/glyphicons/glyphicons_017_music.png">'
	
    },
    displayEvents: function(data){
    	
	if (data){
		var eventsresult = eval(data);
		//eventsresult.results.
	} else {
	    $('#events-display').html('--');
	}
    },
    displayGovFederal: function(data){
		if (data){
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
			// FIXME : multiple value to get price of loaf of bread
			
			$('#price-display').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_227_usd.png"> <div class="dataval">' + data + '</div>');
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