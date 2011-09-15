package lomgr

object Page {
  def apply(body: xml.NodeSeq)(scripts: String*) =
    unfiltered.response.Html(
      <html>
        <head>
          <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
          <title>lom.gr/filmies</title>
          <link href="http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300"
                rel="stylesheet" type="text/css"/>
         <link href="http://fonts.googleapis.com/css?family=Alice" rel="stylesheet" type="text/css"/>
          <link rel="stylesheet" type="text/css" href="/assets/css/filmies.css"/>
      </head>
      <body>
        <div id="container">
          <h1 id="title"><a href="/">
            <span class="lom">lom.gr</span><span class="s">/</span>filmies</a>
          </h1>
         { body }
        </div>
        <script type="text/javascript" src="/assets/js/jquery.min.js"></script>
        {
          scripts.map { src =>
            <script type="text/javascript" src={"/assets/js/%s.js" format src}>
            </script>
          }
         }
      </body>
    </html>)
}

object Pages {
  private def layout(body: xml.NodeSeq)(scripts: String*) = Page(body)(scripts:_*)

  def index(errors: Seq[String] = Nil, msgs: Seq[String] = Nil) = layout(
    <div>
      <div id="msgs">{ msgs.map(m => <div></div>) }</div>
      <div id="errors">{ errors.map(e => <div>{e}</div>) }</div>
      <form action="/up" method="POST" enctype="multipart/form-data">
        <div id="photo">
          <label for="f">Upload your uncut film</label>
          <input type="file" name="f"/>
        </div>
        <div id="cut">
          <label for="cut">cut for</label>
          <select name="cut">
            <option value="default">pick a camera</option>
            <option value="okto">oktomat</option>
            <option value="pop9">pop9</option>
            <option value="actionsampler">actionsampler</option>
            <option value="supersampler">supersampler</option>
          </select>
        </div>
        <div id="speed">
          <label for="speed">in
            <select name="speed">
              <option value="default">pick a speed</option>
              <option value="slomo">slo-mo</option>
              <option value="medio">med-io</option>
              <option value="speedio">speed-io</option>
            </select>
            speed
          </label>
        </div>
        <input id="up-submit" class="btn" type="submit" value="make a filmie" />
      </form>
    </div>
  )("index")

  def show(gif: String, frames: Seq[Frame]) = layout(
   <div>
    <div id="filmie-gif">
      <img title="filmie" src={gif}/>
    </div>
    <form id="publish-film" action="/films" method="POST">
      <input type="hidden" name="file" value={gif}/>
      <input type="submit" value="Publish" class="btn publish" />
    </form>
    <form id="update-film" action="/films" method="POST">
      <input type="hidden" name="file" value={gif}/>
      <input type="hidden" name="_method" value="PUT"/>
      <select name="frames" multiple="true">
        {
          frames map { f =>
            <option selected="true" value={ "%s|%s" format (f.url, f.index) }/>
          }
      }
      </select>
      <input type="submit" value="Apply Changes" class="btn refresh"/>
    </form>
    <form action="/films" method="POST">
      <input type="hidden" name="_method" value="DELETE"/>
      <input type="hidden" name="file" value={gif}/>
      <input type="submit" value="Scrap" class="btn"/>
     </form>
     <p class="instructions">Select and reaggrage frames for your reel and click <strong>Apply Changes</strong></p>
    <ul id="reel">
    {
      frames map { f =>
        <li class={if(f.included) "selected" else ""}>
          <img src={f.url}/><div class="i">{f.index + 1}</div>
        </li>
      }
    }
    </ul>
   </div>
  )("jquery-ui.min", "show")

  def failedWrite(err: String) = layout(
    <div>{err}</div>
  )()

}
