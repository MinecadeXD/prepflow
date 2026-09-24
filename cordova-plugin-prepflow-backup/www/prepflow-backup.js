var PrepFlowBackup = {
  exportBackup: function(backupJson, filename, success, error) {
    cordova.exec(success || function(){}, error || function(){},
      "PrepFlowBackup", "exportBackup", [backupJson, filename]);
  },

  importBackup: function(success, error) {
    cordova.exec(success || function(){}, error || function(){},
      "PrepFlowBackup", "importBackup", []);
  }
};

document.addEventListener("DOMContentLoaded", function () {
  if (window.__prepFlowBackupUiInstalled) return;
  window.__prepFlowBackupUiInstalled = true;

  var headerActions = document.querySelector("header .flex.items-center.space-x-2");
  if (!headerActions) return;

  var group = document.createElement("div");
  group.className = "flex items-center space-x-1.5";
  group.innerHTML =
    '<button type="button" aria-label="Export Backup" title="Export Backup" ' +
      'class="text-xs font-semibold bg-darksubCard hover:bg-slate-800 text-slate-200 border border-darkborder px-3 py-2 rounded-xl transition-all flex items-center space-x-1.5 shadow-sm">' +
      '<svg class="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">' +
        '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v12m0 0l-4-4m4 4l4-4M5 21h14"/></svg>' +
      '<span class="hidden sm:inline">Export</span>' +
    '</button>' +
    '<button type="button" aria-label="Import Backup" title="Import Backup" ' +
      'class="text-xs font-semibold bg-darksubCard hover:bg-slate-800 text-slate-200 border border-darkborder px-3 py-2 rounded-xl transition-all flex items-center space-x-1.5 shadow-sm">' +
      '<svg class="w-4 h-4 text-brand-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">' +
        '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 21V9m0 0l-4 4m4-4l4 4M5 3h14"/></svg>' +
      '<span class="hidden sm:inline">Import</span>' +
    '</button>';

  headerActions.insertBefore(group, headerActions.firstChild);

  var exportButton = group.querySelector("button[aria-label='Export Backup']");
  var importButton = group.querySelector("button[aria-label='Import Backup']");

  function setBusy(button, busy) {
    button.disabled = busy;
    button.style.opacity = busy ? "0.6" : "1";
  }

  function makeBackup() {
    var raw = localStorage.getItem("msc_11th_cet_tracker_data_v3");
    if (!raw) raw = JSON.stringify({});

    var parsed;
    try {
      parsed = JSON.parse(raw);
    } catch (e) {
      alert("PrepFlow data could not be read for backup.");
      return null;
    }

    return JSON.stringify({
      app: "PrepFlow",
      backupVersion: 1,
      createdAt: new Date().toISOString(),
      storageKey: "msc_11th_cet_tracker_data_v3",
      data: parsed
    }, null, 2);
  }

  exportButton.addEventListener("click", function () {
    var backup = makeBackup();
    if (!backup) return;

    setBusy(exportButton, true);
    var date = new Date();
    var stamp = date.getFullYear() + "-" +
      String(date.getMonth() + 1).padStart(2, "0") + "-" +
      String(date.getDate()).padStart(2, "0");
    var filename = "PrepFlow_Backup_" + stamp + ".json";

    if (!window.PrepFlowBackup) {
      alert("Backup is available only in the Android app build.");
      setBusy(exportButton, false);
      return;
    }

    window.PrepFlowBackup.exportBackup(
      backup,
      filename,
      function () {
        setBusy(exportButton, false);
        alert("Backup saved successfully.");
      },
      function (message) {
        setBusy(exportButton, false);
        if (message !== "cancelled") alert("Backup could not be saved.");
      }
    );
  });

  importButton.addEventListener("click", function () {
    if (!window.PrepFlowBackup) {
      alert("Restore is available only in the Android app build.");
      return;
    }

    setBusy(importButton, true);
    window.PrepFlowBackup.importBackup(
      function (text) {
        setBusy(importButton, false);

        var backup;
        try {
          backup = JSON.parse(text);
        } catch (e) {
          alert("This file is not valid JSON.");
          return;
        }

        if (!backup || backup.app !== "PrepFlow" ||
            backup.backupVersion !== 1 ||
            !backup.data || typeof backup.data !== "object") {
          alert("This is not a valid PrepFlow backup file.");
          return;
        }

        if (!confirm("Import this backup? Your current PrepFlow data will be replaced.")) {
          return;
        }

        try {
          localStorage.setItem("msc_11th_cet_tracker_data_v3", JSON.stringify(backup.data));
          location.reload();
        } catch (e) {
          alert("The backup could not be restored.");
        }
      },
      function (message) {
        setBusy(importButton, false);
        if (message !== "cancelled") alert("Backup could not be opened.");
      }
    );
  });
});
