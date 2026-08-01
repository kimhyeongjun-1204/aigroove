import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
import torchaudio
import librosa
import numpy as np
import json
import numpy as np
import random

class SimpleLSTM(nn.Module):
    def __init__(self, input_size=128, hidden_size=256, output_size=128, num_layers=2):
        super().__init__()
        self.lstm = nn.LSTM(input_size, hidden_size, batch_first=True)
        self.fc = nn.Linear(hidden_size, output_size)
    
    def forward(self, x):
        out, _ = self.lstm(x)
        return torch.sigmoid(self.fc(out))  # (B, T, 128)
        #return self.fc(out)  # [B, T, 128]