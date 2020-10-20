/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.exoplayer2.e2etest;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.robolectric.PlaybackOutput;
import com.google.android.exoplayer2.robolectric.ShadowMediaCodecConfig;
import com.google.android.exoplayer2.robolectric.TestPlayerRunHelper;
import com.google.android.exoplayer2.testutil.AutoAdvancingFakeClock;
import com.google.android.exoplayer2.testutil.DumpFileAsserts;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

/** End-to-end tests using MP4 samples. */
// TODO(b/143232359): Remove once https://issuetracker.google.com/143232359 is resolved.
@Config(sdk = 29)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class Mp4PlaybackTest {

  // TODO: Add samples with >2 audio channels when supported (sample_ac3_fragmented.mp4,
  //       sample_ac3.mp4sample_eac3.mp4, sample_eac3_fragmented.mp4, sample_eac3joc.mp4,
  //       sample_eac3joc_fragmented.mp4).
  @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
  public static ImmutableList<String[]> params() {
    return ImmutableList.of(
        new String[] {"midroll-5s.mp4"},
        new String[] {"postroll-5s.mp4"},
        new String[] {"preroll-5s.mp4"},
        new String[] {"sample_ac4_fragmented.mp4"},
        new String[] {"sample_ac4.mp4"},
        new String[] {"sample_android_slow_motion.mp4"},
        new String[] {"sample_fragmented.mp4"},
        new String[] {"sample_fragmented_seekable.mp4"},
        new String[] {"sample_fragmented_sei.mp4"},
        new String[] {"sample_mdat_too_long.mp4"},
        new String[] {"sample.mp4"},
        new String[] {"sample_opus_fragmented.mp4"},
        new String[] {"sample_opus.mp4"},
        new String[] {"sample_partially_fragmented.mp4"},
        new String[] {"testvid_1022ms.mp4"});
  }

  @ParameterizedRobolectricTestRunner.Parameter public String inputFile;

  @Rule
  public ShadowMediaCodecConfig mediaCodecConfig =
      ShadowMediaCodecConfig.forAllSupportedMimeTypes();

  @Test
  public void test() throws Exception {
    SimpleExoPlayer player =
        new SimpleExoPlayer.Builder(ApplicationProvider.getApplicationContext())
            .setClock(new AutoAdvancingFakeClock())
            .build();
    player.setVideoSurface(new Surface(new SurfaceTexture(/* texName= */ 1)));
    PlaybackOutput playbackOutput = PlaybackOutput.register(player, mediaCodecConfig);

    player.setMediaItem(MediaItem.fromUri("asset:///media/mp4/" + inputFile));
    player.prepare();
    player.play();
    TestPlayerRunHelper.runUntilPlaybackState(player, Player.STATE_ENDED);
    player.release();

    DumpFileAsserts.assertOutput(
        ApplicationProvider.getApplicationContext(),
        playbackOutput,
        "playbackdumps/mp4/" + inputFile + ".dump");
  }
}
