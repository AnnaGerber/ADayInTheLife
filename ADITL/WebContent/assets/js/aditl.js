
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
    getData: function(location, date){
	aditl.displayHeader(location,date);
	// request json data for location and date and display results

	aditl.displayPhotos();
	aditl.displayPopulation();
	aditl.displayWeather();
	aditl.displayMusicCharts();
	aditl.displayEvents();
	aditl.displayGov();
	aditl.displayPrice();
    },
    displayHeader: function(location,date){
	$('#location-display').html(location);
	$('#date-display').html($.format.date(date, 'dd MMMM yyyy'));

    },
    displayPhotos: function(data){
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
    },
    displayPopulation: function(data){
	if (data){
	} else {
	    $('#population-display').html('--');
	}
    },
    displayWeather: function(data){
	if (data){
	} else {
	    $('#weather-display').html('--');
	}
    }, 
    displayMusicCharts: function(data){
	if (data){
	} else {
	    $('#music-display').html('--');
	}
	// assume data > song > title
	//	$('#musicCharts').html(
      //     '<img src="../assets/img/glyphicons/glyphicons_017_music.png">'
	
    },
    displayEvents: function(data){
	if (data){
	} else {
	    $('#events-display').html('--');
	}
    },
    displayGov: function(data){
	if (data){
	} else {
	    $('#gov-display').html('--');
	}
    },
    displayPrice: function(data){
	if (data){
	} else {
	    $('#price-display').html('--');
	}
    },
}