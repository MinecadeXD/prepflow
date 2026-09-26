var PrepFlowBackup = {
  exportBackup: function(backupJson, filename, success, error) {
    cordova.exec(
      success || function(){},
      error || function(){},
      "PrepFlowBackup",
      "exportBackup",
      [backupJson, filename]
    );
  },

  importBackup: function(success, error) {
    cordova.exec(
      success || function(){},
      error || function(){},
      "PrepFlowBackup",
      "importBackup",
      []
    );
  }
};

module.exports = PrepFlowBackup;
