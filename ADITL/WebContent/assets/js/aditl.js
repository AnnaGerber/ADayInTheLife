
var aditl = {
	missingDataCount: 0
,	photosAvailable: false
, defaultLocation: "Sydney"
, defaultDate: new Date(Date.parse('1986-05-01'))
	/** action triggered by go button: search on selected date and location **/
, search: function() {
    var date = new Date(Date.parse($('#datepicker').val()));
    var formattedDate = date.format("yyyy-mm-dd");
    var location = $('#location').val();
  
    document.location.href = document.location.pathname+ "#" + escape(location + "+" + formattedDate);
  }
  /** This function does all the magic, this is called everytime the hash part of the URL
   * changes, this then pulls the appropriate parts of the URL and sets the page and AJAX 
   * calls up.
   */
, searchFromURL: function() {
    if (document.location.href.match("#")) {
      var searchParam = document.location.href.split("#")[1].split("+");
      $('#location').val(searchParam[0]);
      var searchday = new Date(Date.parse(searchParam[1]));
      $('#datepicker').datepicker('setDate', searchday);
      aditl.getData($('#location').val(), searchday);
    } else {
      $('#location').val('Sydney');
      $('#datepicker').datepicker('setDate', aditl.defaultDate);
      aditl.getData(aditl.defaultLocation, aditl.defaultDate);
    }
    // Update the FB like button URL so it include the hash part of the URL.
    $('.fb-like').prop('data-href', document.location.href);
    
    // Set the location for the next link.
    var nextDate = new Date(Date.parse($('#datepicker').val()) + 86400000);
    var nextFormattedDate = nextDate.format("yyyy-mm-dd");
    $('#linkNext').prop('href', document.location.pathname+ "#" + escape($('#location').val() + "+" + nextFormattedDate));
    // Set the location for the previous link.
    var prevDate = new Date(Date.parse($('#datepicker').val()) - 86400000);
    var prevFormattedDate = prevDate.format("yyyy-mm-dd");
    $('#linkPrevious').prop('href', document.location.pathname+ "#" + escape($('#location').val() + "+" + prevFormattedDate));
  }
, lookupState: function(location) {
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
  }
, getData: function(location, date) {
    // Reset the variables used for the unavailable message
    aditl.missingDataCount = 0;
    aditl.photosAvailable = false;
    	
    var formattedDate = date.format("yyyy-mm-dd");
		var state = aditl.lookupState(location);
		
		aditl.displayHeaders(location,state,date);
		
		// dbpedia events 
		$.ajax({
			url: "Events?date=" + formattedDate + "&city=" + location,
			success: function(d) {
				aditl.displayEvents(d);
			}
		});
		
		// NAA photos
		$.ajax({
			url: "Photosearch?date=" + formattedDate + "&keyword=" + location,
			complete: function(d) {
				//console.log("Got stories",d);
				aditl.displayStories(d.responseText, formattedDate);
			}
		});
		
		// ABS price index
		$.ajax({
			url: "CPI?date=" + formattedDate + "&state=" + state,
			success: function(d) {
				aditl.displayPrice(d);
			}
		});
		
		// ABS population - females
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=female",
			success: function(d) {
				aditl.displayPopulationFemale(d);
			}
		});
		
		// ABS population - males
		$.ajax({
			url: "Population?date=" + formattedDate + "&state=" + state + "&gender=male",
			success: function(d) {
				aditl.displayPopulationMale(d);
			}
		});
		
		// BOM temperature - min
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=min",
			success: function(d) {
				aditl.displayTemperatureMin(d);
			}
		});
		
		// BOM temperature - max
		$.ajax({
			url: "Temperature?date=" + formattedDate + "&state=" + state + "&minormax=max",
			success: function(d) {
				aditl.displayTemperatureMax(d);
			}
		});
		
		// BOM rainfall
		$.ajax({
			url: "Rainfall?date=" + formattedDate + "&state=" + state,
			success: function(d) {
				aditl.displayRainfall(d);
			}
		});
		
		$.ajax({
			url: "PrimeMinister?date=" + formattedDate,
			success: function(d) {
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
  }
, displayHeaders: function(location, state, date) {
    $('.location-display').html(location +", " || "");
		$('.state-display').html(state || "");
		$('.date-display').html($.format.date(date, 'dd MMMM yyyy') || "");
		$('.year-display').html(date.getFullYear());
		
		// display loading icons for the slow loading results
		$('#gov-display').html('');
		$('#events-display').html('<img src="assets/img/ajax-loader.gif">');
		//$('#stories-display').html('<img src="assets/img/ajax-loader.gif">');
    }
, displayStories: function(data, formattedDate) {
    try {
			if (data) {
				aditl.photosAvailable = true;
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
				aditl.addMissingData('#stories-display');
			}
    	} catch (ex){
    		$('#stories-display').html('There was a problem loading images: ' + ex.message);
    	}
    }
, displayPopulationFemale: function(data) {
		if (data) {
			$('#population-display-f').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_035_woman.png"> &nbsp; <div class="dataval">' + aditl.formatNumber(data) + '</div>');
		} else {
		    aditl.addMissingData('#population-display-f');
	  }
  }
, displayPopulationMale: function(data) {
		if (data) {
			$('#population-display-m').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_034_old_man.png"> &nbsp; <div class="dataval">' + aditl.formatNumber(data) + '</div>');
		} else {
		    aditl.addMissingData('#population-display-m');
		}
  }
, displayTemperatureMin: function(data) {
		if (data) {
			$('#weather-display-min').html('<div class="dataval">' + data + ' &#176;C</div>');
		} else {
		    aditl.addMissingData('#weather-display-min');
		}
  }
, displayTemperatureMax: function(data) {
		if (data) {
			$('#weather-display-max').html('<div class="dataval"> &ndash; ' + data + ' &#176;C</div>');
		} else {
		    aditl.addMissingData('#weather-display-max');
		}
  }
, displayRainfall: function(data) {
    	var rainfall = parseInt(data);
    	var icon = "<img class='dataicon' src='assets/img/iconic-black/sun_fill_24x24.png'>";
    	if (rainfall >= 1) {
    		icon = "<img class='dataicon' src='assets/img/iconic-black/rain_24x21.png'>";
    	}
		if (data){
			$('#weather-display-rain').html(icon + '<div class="dataval">&nbsp;' + data + ' mm</div>');
		} else {
		    aditl.addMissingData('#weather-display-rain');
		}
  }
, displayMusicCharts: function(data) {
    if (data) {
      // '<img src="assets/img/glyphicons/glyphicons_017_music.png">'
		} else {
		  aditl.addMissingData('#music-display');
		}
  }
, displayEvents: function(data) {
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
					aditl.addMissingData('#events-display');
				}
			} else {
				aditl.addMissingData('#events-display');
			}
    } catch (ex){
      aditl.addMissingData('#events-display');
    }
  }
, displayGovFederal: function(data) {
    if (data) {
			var pmdata = eval(data);
			if (pmdata && pmdata.ministry) {
				var pm = pmdata.ministry || "";
				var party = pmdata.party || "";
				
				$('#gov-display').html("<img class='dataicon' src='assets/img/glyphicons/glyphicons_263_bank.png'>  <span class='dataval'>" + pm + "</span><br/>" +
						"<span style='font-size:small' class='dataval'>" + party + "</span>");
			} else {
				aditl.addMissingData('#gov-display');
			}
		} else {
			aditl.addMissingData('#gov-display');
		}
  }
, displayGovState: function(data) {
    if (data) {
		} else {
		  aditl.addMissingData('#gov-display');
    }
  }
, displayPrice: function(data) {
		if (data) {
			//  multiply value to get price of loaf of bread - $2.50 in 2012
			var f = parseFloat(data) * 2.5;
			f = f.toFixed(2);
			$('#price-display').html('<img class="dataicon" src="assets/img/glyphicons/glyphicons_227_usd.png"> <div class="dataval">' + f + '</div>');
		} else {
		    aditl.addMissingData('#price-display');
		}
  }
, formatNumber: function(num) {
   if (num) {
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
, addMissingData: function(selector) {
  	$(selector).html('--');
  	aditl.missingDataCount++;
   	if (aditl.missingDataCount > 4 && !aditl.photosAvailable) {
   		var unavailableMessage = 
   			"<blockquote><h2>Sorry, we don't know enough about life on this date.</h2>" +
              "<small>A day in the life tries to reconnect you with your past. Try putting in your birthday, friends and families birthdays or a significant event in your life such as the day you got married.</small></blockquote>";
    	$('#stories-display').html(unavailableMessage);
    }
  } 
};

$(window).load(function() {
  // Using the jQuery hashchange plugin, watch for changes to the hash part of the URL
  // and re-query the data.
  $(window).hashchange(function() {
    aditl.searchFromURL();
  });
  // On initial load trigger the hashchange in case parameters have been passed in.
  $(window).hashchange();
});