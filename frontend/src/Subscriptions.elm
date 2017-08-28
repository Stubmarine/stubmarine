module Subscriptions exposing (subscriptions)

import Model exposing (Model, Route(Inbox))
import Message exposing (Msg, Msg(WSEmailsMessage))
import WebSocket

subscriptions : Model -> Sub Msg
subscriptions model =
  let
    url = \inboxName -> model.wsapiBasePath ++ "/wsapi/inbox/" ++ inboxName ++ "/emails"

    email = case model.route of
      Inbox inboxName _ ->
         WebSocket.listen (url inboxName) WSEmailsMessage
      _ ->
        Sub.none
  in
    email
