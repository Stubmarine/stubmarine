module View exposing (view)

import Html exposing (Html, div, text, dd, dt, dl, hr, span, ul, li)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)

import RemoteData exposing (RemoteData(..))

import Model exposing (..)
import Message exposing (..)

viewEmailListItem : Email -> Html Msg
viewEmailListItem email =
  div [ class "email", onClick (SelectEmail email.id) ]
    [ div [] [ text "From: ", text email.from ]
    , div [] [ text "To: ", text email.to ]
    , div [] [ text "Subject: ", text email.subject ]
    ]

view : Model -> Html Msg
view model =
  let
    emails = case model.emails of
      Success emailsList ->
        List.map viewEmailListItem emailsList
      Loading ->
        [ div [] [ text "Loading..." ] ]
      _ ->
        [ div [] [] ]
    emailList = [ div [ class "email-list" ] emails ]
    emailDetail = case model.email of
      NotAsked ->
        []
      Loading ->
        [ div [ class "email-detail" ] [ text "Loading..." ] ]
      Success email ->
        [ div [ class "email-detail" ]
          [ dl []
            [ dt [] [ text "From:" ]
            , dd [] [ text email.from ]
            , dt [] [ text "To:" ]
            , dd [] [ text email.to ]
            , dt [] [ text "Subject:" ]
            , dd [] [ text email.subject ]
            , hr [] []
            , div [ class "email-detail--body" ] [ text email.body ]
            ]
          ]
        ]
      Failure err ->
        [ div [ class "email-detail" ] [ text "Error" ] ]
  in

    div []
      [ div [ class "nav-bar" ]
        [ div [ class "container container__nav" ]
          [ div [ class "logo" ] [ text "Wallraff" ]
          , ul [ class "nav" ]
            [ li [ class "nav-item" ] [ text "Emails"]
            ]
          ]
        ]
      , div [ class "page-content" ]
          [ div [ class "email-pane" ] (emailList ++ emailDetail)
          ]
      ]