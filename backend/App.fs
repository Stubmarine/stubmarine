open Suave
open Suave.Filters
open Suave.Operators

open System

[<EntryPoint>]
let main _ =
    let port = Environment.GetEnvironmentVariable "PORT"
    let local = Suave.Http.HttpBinding.createSimple HTTP "0.0.0.0" (int port)
    let config = {defaultConfig with bindings = [local]}
    let app : WebPart =
        choose [
            GET >=> path "/" >=> Files.file "../frontend/dist/index.html"
            RequestErrors.NOT_FOUND "Page not found."
        ]
    startWebServer config app
    0

