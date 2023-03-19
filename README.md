# HRSystem
HR system with OCR technology As university graduation work

The work flow for this project is as follows.

## 1. A user requests registration via a PDF or image on the web.
1. There are sign in form to regist a new data.
- must sign in to regist or look up. 
2. There are three section of web.
- register section. (need to upload input file to backend server)
- confirming section
- looking up section.

## 2. The web server sends a recognition request to the OCR server.
1. upload the input file to OCR server(file server).
2. The web server requests recognition of the document to the OCR server.
    - There is custom setting for each-format of documnet.
    - polling wait(asynchronous OCR API).
3. get result data from OCR server(file server).
4. parse and insert valid data to spring database application (DB server).