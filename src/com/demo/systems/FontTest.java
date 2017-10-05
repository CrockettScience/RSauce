package com.demo.systems;

import com.Preferences;
import com.sauce.asset.fonts.Font;
import com.sauce.core.engine.DrawSystem;
import com.sauce.core.engine.Engine;
import com.util.Color;

public class FontTest extends DrawSystem {

    private Font spider;
    private static int X = Preferences.getScreenWidth() / 2;
    private static int Y = Preferences.getScreenHeight() / 4;

    public FontTest(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        spider = new Font(Preferences.ASSET_ROOT + "spider.ttf", 196);
    }

    @Override
    public void update(double delta) {
        spider.renderText("RSauce!", Color.C_WHITE, X - spider.getStringWidth("RSauce!") / 2, Y - spider.getHeight() / 2);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        spider.dispose();
    }
}
