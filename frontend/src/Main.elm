import Html exposing (Html, div, text)

type Msg = HelloWorld

type alias Model = Int


update : Msg -> Model -> Model
update msg model =
  case msg of
    HelloWorld ->
      model

view : Model -> Html Msg
view model =
  div [] [
    text "Hello World" ]

main : Program Never Int Msg
main =
  Html.beginnerProgram { model = 0, view = view, update = update }