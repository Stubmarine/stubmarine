'use strict';

require('./css/main.scss');

// Require index.html so it gets copied to dist
require('./index.html');

const Elm = require('./Main.elm');
const mountNode = document.getElementById('main');

// .embed() can take an optional second argument. This would be an object describing the data we need to start a program, i.e. a userID or some token
var app = Elm.Main.embed(mountNode);

Notification.requestPermission().then(function(result) {
});

app.ports.notifyBrowser.subscribe(function(browserNotification) {
    if (Notification.permission === "granted") {
        new Notification(browserNotification.title, {
            body: browserNotification.body
        });
    }
});
