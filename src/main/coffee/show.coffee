$ () ->
  updateForm = ->
    console.log "updating form"
    up = $ "#update-film"
    frames = up.find("select[name='frames']")
    reel = $('#reel li')
    selected = []
    reel.each (i, r) ->
      if $(r).hasClass 'selected'
        selected.push [$(r), i]

    frames.empty()
    opts = ('<option selected="true" value="' + s[0].find('img').attr('src')+'|'+ s[1].toString() + '"/>' for s in selected).join('')
    frames.append(opts)

  $("#reel").sortable(
    change: updateForm,
    opacity: 0.75
  )
  $("#reel li").click ->
    ls = $(@)
    sel = 'selected'
    if ls.hasClass sel
      ls.toggleClass sel
      ls.find('img').css('opacity', '0.5')
    else
      ls.toggleClass sel
      ls.find('img').css('opacity', '1')
    updateForm()
