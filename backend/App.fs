open Suave
open Suave.Filters
open Suave.Operators

open System
open System.IO

[<EntryPoint>]
let main _ =
    let port = Environment.GetEnvironmentVariable "PORT"
    let local = Suave.Http.HttpBinding.createSimple HTTP "0.0.0.0" (int port)
    let config = {defaultConfig with bindings = [local]; homeFolder = Some (Path.GetFullPath "./static")}
    let app : WebPart =
        choose [
            GET >=> path "/" >=> Files.file "./static/index.html"
            GET >=> Files.browseHome
            RequestErrors.NOT_FOUND "Page not found."
        ]
    startWebServer config app
    0

