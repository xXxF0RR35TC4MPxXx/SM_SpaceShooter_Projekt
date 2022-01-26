package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.SpaceGame;

public class AndroidLauncher extends AndroidApplication {
	public int orientation;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		orientation = getResources().getConfiguration().orientation;
		initialize(new SpaceGame(), config);
	}
}
