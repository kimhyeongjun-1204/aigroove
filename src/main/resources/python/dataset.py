import numpy as np
import torch
from torch.utils.data import Dataset, DataLoader
import librosa  
import os
import sys
from pathlib import Path
import pretty_midi

import os
import torch
import numpy as np
import librosa
from torch.utils.data import Dataset

SR = 22050
HOP_LENGTH = 512
N_MELS = 128
DURATION = 10  # seconds

class AudioMIDIDataset(Dataset):
    def __init__(self, audio_root_dirs, sample_rate=16000, n_mels=128, hop_length=512):
        self.samples = []
        for root_dir in audio_root_dirs:
            for fname in os.listdir(root_dir):
                if fname.endswith('.flac'):
                    base = fname[:-5]
                    audio_path = os.path.join(root_dir, base + '.flac')
                    midi_path = os.path.join(root_dir, base + '.mid')
                    if os.path.exists(midi_path):
                        self.samples.append((audio_path, midi_path))
                      
        self.mel_specs = []
        self.labels = []
        for audio_path, midi_path in self.samples:
            y, _ = librosa.load(audio_path, sr=SR, duration=DURATION)
            mel = librosa.feature.melspectrogram(y=y, sr=SR, n_mels=N_MELS, hop_length=HOP_LENGTH)
            mel_db = librosa.power_to_db(mel).astype(np.float32)

            midi = pretty_midi.PrettyMIDI(midi_path)
            piano_roll = np.zeros((mel_db.shape[1], 128), dtype=np.float32)
            for note in midi.instruments[0].notes:
                start = int(note.start * SR / HOP_LENGTH)
                end = int(note.end * SR / HOP_LENGTH)
                if end > piano_roll.shape[0]: continue
                piano_roll[start:end, note.pitch] = 1.0

            self.mel_specs.append(torch.tensor(mel_db.T))      # [T, mel]
            self.labels.append(torch.tensor(piano_roll))       # [T, 128]

    def __len__(self): return len(self.mel_specs)

    def __getitem__(self, idx): return self.mel_specs[idx], self.labels[idx]
    