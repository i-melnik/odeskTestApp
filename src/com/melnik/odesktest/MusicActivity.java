package com.melnik.odesktest;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.melnik.odesktest.adapter.MusicListAdapter;
import com.melnik.odesktest.entity.MusicItem;
import com.melnik.odesktest.menu.MySlidingMenu;

public class MusicActivity extends SherlockFragmentActivity implements OnPreparedListener, OnCompletionListener
{

	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;

	private ImageButton play;
	private ImageButton previous;
	private ImageButton next;

	private List<MusicItem> data;

	int currentPositionPlaying;

	private ListView listView;

	private SlidingMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_music);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		listView = (ListView) findViewById(R.id.music_list);

		play = (ImageButton) findViewById(R.id.music_play);
		previous = (ImageButton) findViewById(R.id.music_previous);
		next = (ImageButton) findViewById(R.id.music_next);

		previous.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (currentPositionPlaying > 0)
				{
					mediaPlayer.stop();
					onCompletion(mediaPlayer);
					play(currentPositionPlaying - 1);
				}
			}
		});

		next.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (currentPositionPlaying < data.size() - 2)
				{
					mediaPlayer.stop();
					onCompletion(mediaPlayer);
					play(currentPositionPlaying + 1);
				}
			}
		});

		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null)
		{
			// query failed, handle error.
		}
		else if (!cursor.moveToFirst())
		{
			// no media on the device
		}
		else
		{
			int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			int displayNameColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DISPLAY_NAME);
			int dataColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			data = new ArrayList<MusicItem>();
			do
			{
				MusicItem item = new MusicItem();
				long thisId = cursor.getLong(idColumn);
				String thisTitle = cursor.getString(titleColumn);
				String displayName = cursor.getString(displayNameColumn);
				String artist = cursor.getString(artistColumn);
				String album = cursor.getString(albumColumn);
				String dataString = cursor.getString(dataColumn);

				item.setTitle(thisTitle);
				item.setName(displayName);
				item.setAlbum(album);
				item.setArtist(artist);
				item.setDataUrl(dataString);
				data.add(item);
			} while (cursor.moveToNext());

			listView.setAdapter(new MusicListAdapter(this, data));
		}

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				play(position);
			}
		});
		menu = MySlidingMenu.getMenu(this, getString(R.string.menu_solutions));
		showActionBar();

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		releaseMP();
	}

	private void play(int position)
	{
		MusicItem item = (MusicItem) listView.getItemAtPosition(position);
		currentPositionPlaying = position;
		mediaPlayer = new MediaPlayer();
		try
		{
			mediaPlayer.setDataSource(item.getDataUrl());
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnPreparedListener(MusicActivity.this);
			mediaPlayer.setOnCompletionListener(MusicActivity.this);
			mediaPlayer.prepareAsync();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void showActionBar()
	{
		// getSupportActionBar().setCustomView(R.layout.profile_action_bar);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.profile_action_bar, null);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(v);

		ImageButton menuButton = (ImageButton) v.findViewById(R.id.menuButton);
		menuButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				menu.toggle();
			}
		});
	}

	private void releaseMP()
	{
		if (mediaPlayer != null)
		{
			try
			{
				mediaPlayer.release();
				mediaPlayer = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPrepared(final MediaPlayer mp)
	{
		mp.start();
		play.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
		play.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mp.stop();
				onCompletion(mp);
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		releaseMP();
	}

	@Override
	public void onCompletion(final MediaPlayer mp)
	{
		play.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
		play.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mp.start();
			}
		});
	}
}
