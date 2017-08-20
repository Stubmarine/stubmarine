module Subscriptions exposing (subscriptions)

import Model exposing (Model)
import Message exposing (Msg, Msg(WSEmailsMessage))
import WebSocket

subscriptions : Model -> Sub Msg
subscriptions model =
  let
    url = "ws://" ++ model.apiHost ++ "/wsapi/emails"
  in
    WebSocket.listen url WSEmailsMessage
