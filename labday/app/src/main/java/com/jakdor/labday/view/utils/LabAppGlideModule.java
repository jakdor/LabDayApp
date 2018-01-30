package com.jakdor.labday.view.utils;

import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.annotation.GlideModule;

@GlideModule
public final class LabAppGlideModule extends AppGlideModule {

    // Disable manifest parsing to avoid adding similar modules twice.
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
