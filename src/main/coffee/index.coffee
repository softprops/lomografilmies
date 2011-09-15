$ = jQuery
$ () ->
  $('#photo').change (e) ->
    $("#cut").fadeIn () ->
      $(@).change (e) ->
        $("#speed").fadeIn () ->
          $(@).change (e) ->
            $("#up-submit").show()