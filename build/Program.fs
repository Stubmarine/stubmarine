open SharpScss
open System
open System.IO


let private writeToFile (filePath : string) (content : string) =
    Directory.GetParent(filePath).Create()

    use writer = File.CreateText(filePath)
    writer.WriteLine(content)

let private generateCss (filePath : string) : string =
    let scssOptions = new ScssOptions (OutputStyle = ScssOutputStyle.Compressed)
    let result = Scss.ConvertFileToCss(filePath, scssOptions)
    result.Css

[<EntryPoint>]
let main (args : string []) =
    generateCss "frontend/scss/app.scss" |> writeToFile "frontend/dist/app.css"
    0

