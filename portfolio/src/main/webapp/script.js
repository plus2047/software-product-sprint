// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

async function getComments() {
    const response = await fetch('/data');
    const message = await response.json();
    console.log(message);
    var commentField = document.getElementById('comments')
    for(i = 0; i < message.length; i ++) {
        var paragraph = document.createElement("P");
        var boldField = document.createElement("B");
        var entity = message[i]
        var name = document.createTextNode(entity["name"] + " said:");
        var text = document.createTextNode(entity["comment"]);
        boldField.appendChild(name);
        paragraph.appendChild(boldField);
        paragraph.appendChild(document.createElement("BR"))
        paragraph.appendChild(text);
        if("image" in entity && entity["image"] != "") {
            var imageField = document.createElement("IMG")
            imageField.src = entity["image"]
            paragraph.appendChild(imageField)
        }
        var imageField = document.createElement("IMG")

        commentField.appendChild(paragraph);
    }
}

function fetchBlobstoreUrlAndShowForm() {
    fetch('/blobstore-upload-url')
        .then((response) => {
          return response.text();
        })
        .then((imageUploadUrl) => {
          const messageForm = document.getElementById('comment-form');
          messageForm.action = imageUploadUrl;
          messageForm.classList.remove('hidden');
        });
}

function init() {
    getComments()
    fetchBlobstoreUrlAndShowForm()
}