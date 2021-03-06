package sauce.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.stb.STBVorbis.*;

class AudioUtil {

    static AudioUtil.IOAudio ioResourceToAudio(ResourceUtil.IOResource resource, AudioInfo info){
        return new IOAudio(resource.getBuffer(), info);
    }

    static AudioInfo getAudioInfo(ResourceUtil.IOResource resource){
        return new AudioInfo(resource.getBuffer());
    }

    static class AudioInfo {
        private long handle;
        private int channels;
        private int sampleRate;
        private int format;

        private int lengthSamples;
        private float lengthSeconds;

        private ShortBuffer pcm;

        private AudioInfo(ByteBuffer buffer){

            IntBuffer error = BufferUtils.createIntBuffer(1);
            handle = stb_vorbis_open_memory(buffer, error, null);
            if (handle == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
                stb_vorbis_get_info(handle, info);
                channels = info.channels();
                sampleRate = info.sample_rate();
            }

            format = getFormat(channels);

            this.lengthSamples = stb_vorbis_stream_length_in_samples(handle);
            this.lengthSeconds = stb_vorbis_stream_length_in_seconds(handle);

            this.pcm = BufferUtils.createShortBuffer(Preferences.AUDIO_BUFFER_SIZE);
        }

        private static int getFormat(int channels) {
            switch (channels) {
                case 1:
                    return AL_FORMAT_MONO16;
                case 2:
                    return AL_FORMAT_STEREO16;
                default:
                    throw new UnsupportedOperationException("Unsupported number of channels: " + channels);
            }
        }

        long getHandle() {
            return handle;
        }

        int getChannels() {
            return channels;
        }

        int getSampleRate() {
            return sampleRate;
        }

        int getFormat() {
            return format;
        }

        int getLengthSamples() {
            return lengthSamples;
        }

        float getLengthSeconds() {
            return lengthSeconds;
        }

        ShortBuffer getPcm() {
            return pcm;
        }
    }

    static class IOAudio extends ResourceUtil.IOResource{

        private AudioInfo audioInfo;

        IOAudio(ByteBuffer aBuffer, AudioInfo info) {
            super(aBuffer);
            audioInfo = info;

        }

        AudioInfo getAudioInfo(){
            return audioInfo;
        }
    }
}
