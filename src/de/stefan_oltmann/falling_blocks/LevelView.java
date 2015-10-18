/*
 * Falling Blocks
 * A simple faling blocks game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
 *
 * Contact : fallingblocks@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
 *
 * This file is part of Falling Blocks.
 *
 * Falling Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Falling Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Falling Blocks. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.falling_blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

/**
 * @author Stefan Oltmann
 */
public class LevelView extends AbstractBoxView {

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Bitmap createBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.box_level);
    }

}
