#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir C:\Program Files\DataImporter ; %A_ScriptDir%  ; Ensures a consistent starting directory.

GetSelectedText() {
	tmp = %ClipboardAll% ; save clipboard
	Clipboard := "" ; clear clipboard
	Send, ^c ; simulate Ctrl+C (=selection in clipboard)	
	ClipWait, 0, 1 ; wait until clipboard contains data
	selection = %Clipboard% ; save the content of the clipboard
	Clipboard = %tmp% ; restore old content of the clipboard
	return selection
}

^!c::
result := GetSelectedText()
StringReplace, result,result,",', All ; replace escape characters
Run C:\Program Files\DataImporter\DataImporter.exe --values="%result%"
; Run, python ./importer.py "%result%"
return