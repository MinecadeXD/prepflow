var exec = require('cordova/exec');

module.exports = {
    create: function (json, fileName, success, error) {
        exec(success || function () {}, error || function () {}, 'SaveBackup', 'create', [json, fileName]);
    }
};
