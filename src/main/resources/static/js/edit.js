$( document ).ready(function() {

    var isNumber = function (n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }
    
    // At the top of the Dialog Box is Error Messages
    $('#error-message').hide();
    $("#message-one").html("<b></b>");
    $("#message-two").html("<b></b>");
    $("#message-three").html("<b></b>");

   // pagingType - The pagination option of DataTables will display a pagination control below the table
   // full_numbers - 'First', 'Previous', 'Next' and 'Last' buttons, plus page numbers
    var dicTable = $('#dictionary-entries').DataTable( {
        "pagingType": "full_numbers"
    } );

    var startButtonsPosition = function(changeButtons){
	
	    var edit_disabled = changeButtons ;
	    var delete_disabled = changeButtons ;
	    var cancel_disabled = changeButtons ;
	    var new_disabled = !( changeButtons );
	    
        $("#edit-entry").prop("disabled",edit_disabled);
        $("#delete-entry").prop("disabled",delete_disabled);
        $("#new-entry").prop("disabled",new_disabled);
        $("#cancel-entry").prop("disabled",cancel_disabled);
        
        console.log("Edit Button Disabled: " + edit_disabled);
        console.log("Delete Button Disabled: " + delete_disabled);
        console.log("New Button Disabled: " + new_disabled);
        console.log("Cancel Button Disabled: " + cancel_disabled);

    };

    startButtonsPosition(true);

    var dataPage = {};
    var dataDatabase = {};
    var action= "";
    var id  = "";

    //Record Click Function
    $('#dictionary-entries').on('click', 'tbody tr', function() {

		//Get Record from the table which is clicked.
		//Put record in an array
        dataPage = dicTable.row( this ).data();
        
        //Show DataTable record click
        console.log("DataTable clicked Record: \n");
        for(var i = 0; i < dataPage.length; i++){
        	console.log("[" + i + "]= " +dataPage[i] ); 
        }

        /*
         If you select a record in the Database the Edit, Delete, and Cancel 
         Buttons will be enable and other button disable (New Button). When click
         the same row in the DataTable the New Button will be enable and other buttons disable
        */
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
            startButtonsPosition(true);
        }
        else {
            dicTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            startButtonsPosition(false);

        }

    });

    $("#new-entry").click(function() {
        $("#new-edit-title").text("New Entry");
        action = "new";

        $("#word").val("");
        $("#pos").val("");
        $("#desc").val("");
        $("#word-usage").val("");
        $("#right-count").val("");
        $("#total-try-count").val("");

        dataDatabase = {};

    });

    $("#edit-entry").click(function() {
        $("#new-edit-title").text("Edit Entry");
        action = "edit";

        $("#word").val(dataPage[1]);
        $("#pos").val(dataPage[2]);
        $("#desc").val(dataPage[3]);
        $("#word-usage").val(dataPage[4]);
        $("#right-count").val(dataPage[5]);
        $("#total-try-count").val(dataPage[6]);

    });



    $("#delete-entry").click(function() {
        startButtonsPosition(true);
        action = "delete";

        dicTable.row('.selected').remove().draw( false );
        
        //The dataPage Array is set in the Record Click Function
        
        console.log("For Record which is going to be DELETE is: " + dataPage[0] );

        $.ajax({
            type: "POST",
            url: "/delete/" +dataPage[0]
        });

    });
    
   $("#cancel-entry").click(function() {
   		startButtonsPosition(true);
   });

    $("#save").click(function(){

        console.log("ACTION: " + action);

        var right = $("#right-count").val();
        var total = $("#total-try-count").val();

        $('#error-message').hide();
        $("#message-one").html("<b></b>");
        $("#message-two").html("<b></b>");
        $("#message-three").html("<b></b>");


        if(isNumber(right) && isNumber(total) && ( parseFloat(total) !== 0) ) {

            dataDatabase["word"] = $("#word").val();
            dataDatabase["partOfSpeech"] = $("#pos").val();
            dataDatabase["definition"] = $("#desc").val();
            dataDatabase["wordUsage"] = $("#word-usage").val();
            dataDatabase["rightCount"] = $("#right-count").val();
            dataDatabase["totalTryCount"] = $("#total-try-count").val();


            if (action === "edit") {
                dicTable.row('.selected').remove().draw(false);
                dataDatabase["id"] = dataPage[0];
            }

            console.log("DATA being saved in the database: " + Object.values(dataDatabase));

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/save",
                data: JSON.stringify(dataDatabase),
                dataType: 'json',
                timeout: 600000,
                success: function (result) {
                    console.log(result);
                    id = result;

                    dicTable.row.add([
                        id,
                        dataDatabase["word"],
                        dataDatabase["partOfSpeech"],
                        dataDatabase["definition"],
                        dataDatabase["wordUsage"],
                        dataDatabase["rightCount"],
                        dataDatabase["totalTryCount"]
                    ]).draw(false);

                    startButtonsPosition(true);

                    $("#close").click();
                }
            });
        }else{
            $('#error-message').show();

            if(!isNumber(right)){
                $("#message-one").html("<b>Right Answers must be number.</b>");
            }

            if(!isNumber(total)){
                $("#message-two").html("<b>Total Tries must be number.</b>");
            }

            if( parseFloat(total) === 0){
                $("#message-three").html("<b>Total Tries can not be zero.</b>");
            }
        }



    });







});

