/*
Copyright 2013 Andrew Joiner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package uk.co.tekkies.readings.model;

public class Passage {
    private String title;
    private String summary;

    public Passage(String title, String summary) {
        this.title = title;
        setSummary(summary);
    }

    public String getSummary() {
        synchronized (this) {
            return summary;
        }
    }

    public void setSummary(String summary) {
        synchronized (this) {
            this.summary = summary;
        }
    }

    public String getTitle() {
        return title;
    }
}
