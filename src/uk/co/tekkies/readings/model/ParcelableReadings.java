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

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableReadings implements Parcelable {

    public static final String PARCEL_NAME = "parcelableReadings";
    public String selected = null;
    public ArrayList<Passage> passages;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(passages.size());
        out.writeString(selected);
        for (Passage passage : passages) {
            out.writeString(passage.getTitle());
            out.writeString(passage.getSummary());
        }
    }

    public ParcelableReadings(ArrayList<Passage> passages, String selected) {
        this.passages = passages;
        this.selected = selected;
    }

    private ParcelableReadings(Parcel in) {
        int size = in.readInt();
        selected = in.readString();
        passages = new ArrayList<Passage>();
        for (int index = 0; index < size; index++) {
            passages.add(new Passage(in.readString(), in.readString()));
        }
    }

    public static final Parcelable.Creator<ParcelableReadings> CREATOR = new Parcelable.Creator<ParcelableReadings>() {
        public ParcelableReadings createFromParcel(Parcel in) {
            return new ParcelableReadings(in);
        }

        public ParcelableReadings[] newArray(int size) {
            return new ParcelableReadings[size];
        }
    };

}
