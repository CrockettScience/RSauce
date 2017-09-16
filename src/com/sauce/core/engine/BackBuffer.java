package com.sauce.core.engine;

import com.sauce.asset.graphics.Surface;
import com.sauce.core.Project;

class BackBuffer extends Surface {
    public BackBuffer() {
        super(Project.INTERNAL_WIDTH, Project.INTERNAL_HEIGHT);
        setStaticMode(true);
    }

    int texID(){
        return textureID();
    }
}
