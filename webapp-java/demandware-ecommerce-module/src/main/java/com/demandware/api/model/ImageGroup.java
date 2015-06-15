/*
 * Copyright 2014 Niclas Cedermalm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demandware.api.model;

import java.util.List;

/**
 * ImageGroup
 *
 * @author nic
 */
public class ImageGroup {

    private List<Image> images;
    private String variation_value;
    private String view_type;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getVariation_value() {
        return variation_value;
    }

    public void setVariation_value(String variation_value) {
        this.variation_value = variation_value;
    }

    public String getView_type() {
        return view_type;
    }

    public void setView_type(String view_type) {
        this.view_type = view_type;
    }
}
