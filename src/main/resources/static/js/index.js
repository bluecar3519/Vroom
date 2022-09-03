$(document).ready(function() {

    var dic = [];
    var row_counter = 0;
    var right_counter = 0;
    var wrong_counter = 0;

    var show_displace = false;

    var table = document.getElementById('dictionary-entries');

    var rowLength = table.rows.length;

    for(var x=0; x<rowLength; x+=1){
        var row = table.rows[x];
        dic[x] = [];

        for(var y = 0; y < 7; y++){
            dic[x][y] = row.cells[y].innerText;

            if( y==5 || y==6){
                dic[x][y] = parseInt( dic[x][y] );
            }
        }

    }

    $('#diplayWordTotal').text("( " + row_counter +" / " + (rowLength -1) + " )");

    var changeProgressStatus = function(progressCount, progressTotalWordCount){
        var progress = progressCount/ progressTotalWordCount;
        var progressBarLength = 346 * (progress);
        $('#goosander-progress').css('width', progressBarLength +'px');
        $('#goosander-progress').html(Math.ceil(progress*100) + '%');
        $('#diplayWordTotal').text("( " + progressCount +" / " + (rowLength -1) + " )");
    };

    var changeProgressTotals = function(){

        $('#diplayWrongTotal').text( "WRONG: " + wrong_counter );
        $('#diplayRightTotal').text( "RIGHT: " + right_counter );
    };

    $('#show-block').hide();
    $('.check-answer').hide();

    $('#done').hide();


    $( "#previousBtn" ).click(function() {
        changeProgressStatus(row_counter,rowLength-1);
        changeProgressTotals();

        $('#answer').val("");

        $('#show-block').hide();
        $('.check-answer').hide();
        show_displace = false;

        if(row_counter == 0){
            row_counter = rowLength -1;
        }else{
            row_counter = row_counter -1;
        }
    });

    $( "#sayBtn" ).click(function() {
        changeProgressStatus(row_counter,rowLength-1);
        changeProgressTotals();

        var word = dic[row_counter][1]

        if('speechSynthesis' in window){
            var speech = new SpeechSynthesisUtterance(word);
            speech.lang = 'en-US';
            window.speechSynthesis.speak(speech);
        }
    });

    $( "#checkBtn" ).click(function() {
        changeProgressStatus(row_counter,rowLength-1);
        changeProgressTotals();

        var user_answer = $('#answer').val();

        //Replace spaces
        user_answer = user_answer.replace(/\s/g, '');
        dic[row_counter][1] = dic[row_counter][1].replace(/\s/g, '');

        //Increase Total Count
        dic[row_counter][5] = dic[row_counter][5] +1;

        if( user_answer.toLowerCase() == dic[row_counter][1].toLowerCase() ){
            $('#correct').show();
            $('#incorrect').hide();

            //Increase Right Count
            dic[row_counter][6] = dic[row_counter][6] +1;

            right_counter = right_counter +1;
        }else{
            $('#incorrect').show();
            $('#correct').hide();

            wrong_counter = wrong_counter +1;
        }

        if(show_displace){
            $( "#showBtn" ).click();
        }

    });


    $( "#showBtn" ).click(function() {
        changeProgressStatus(row_counter,rowLength-1);
        changeProgressTotals();

        show_displace = true;

        $('#show-block').show();
        $('.show-header').show();
        $('#show-word').text( dic[row_counter][1] );
        $('#show-part-speech').text( dic[row_counter][2] );
        $('#show-definition').text( dic[row_counter][3] );
        $('#show-word-usage').text( dic[row_counter][4] );
        $('#show-right-total-count').text( dic[row_counter][6] +'/'+dic[row_counter][5]);

    });

    $( "#nextBtn" ).click(function() {
        changeProgressStatus(row_counter,rowLength-1);
        changeProgressTotals();

        $('#answer').val("");

        $('#show-block').hide();
        $('.check-answer').hide();
        show_displace = false;

        if(row_counter == rowLength -1){
            row_counter = 0;
        }else{
            row_counter = row_counter +1;
        }
    });

    $( "#donetBtn" ).click(function() {

        $('#show-block').hide();
        $('.check-answer').hide();

        for(var i=0; i<rowLength; i+=1) {

            var dataDatabase = {};

            dataDatabase["id"] = dic[i][0];
            dataDatabase["word"] = dic[i][1];
            dataDatabase["partOfSpeech"] = dic[i][2];
            dataDatabase["definition"] = dic[i][3];
            dataDatabase["wordUsage"] = dic[i][4];
            dataDatabase["totalTryCount"] = dic[i][5];
            dataDatabase["rightCount"] = dic[i][6];

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/save",
                data: JSON.stringify(dataDatabase),
                dataType: 'json',
                timeout: 600000,
                success: function (result) {
                    console.log(result);
                }
            });
        }

        $('#done').show();

        $("#done span").html('<strong>Correct</strong> Number row change in database is ' + (rowLength -1));
    });

});