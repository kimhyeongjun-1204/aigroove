from sklearn.metrics import f1_score, accuracy_score
import torch
import numpy as np

@torch.no_grad()
def evaluate_model(model, dataloader, device, threshold=0.5):
    model.eval()
    
    all_preds = []
    all_targets = []

    for batch in dataloader:
        inputs = batch['mel_spec'].to(device)  # [B, C, F, T]
        targets = batch['frame_labels'].to(device)  # [B, T, 88] 같은 형태라고 가정

        outputs = model(inputs)[2]  # frame_logits만 가져옴
        probs = torch.sigmoid(outputs)

        # 이진화
        preds = (probs > threshold).int()
        targets = targets.int()

        # flatten: [B*T, 88] → sklearn과 호환
        all_preds.append(preds.view(-1, preds.shape[-1]).cpu().numpy())
        all_targets.append(targets.view(-1, targets.shape[-1]).cpu().numpy())

    y_pred = np.concatenate(all_preds, axis=0)
    y_true = np.concatenate(all_targets, axis=0)

    # 다중 클래스 멀티라벨 기준: average='macro'
    f1 = f1_score(y_true, y_pred, average='macro', zero_division=0)
    acc = accuracy_score(y_true, y_pred)

    return {
        'f1_score': f1,
        'accuracy': acc
    }
