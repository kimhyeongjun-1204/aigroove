import argparse
import os
from openai import OpenAI
import base64
import logging
import sys
import traceback

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    stream=sys.stderr  # stderr로 출력하여 Java에서 에러 스트림으로 캡처
)

def generate_image(prompt, output_path):

    print(f"Generating image with prompt: '{prompt}', size: 1024x1024")

    # API 키는 환경변수에서 읽는다 (소스에 하드코딩하지 않음)
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        raise RuntimeError(
            "환경변수 OPENAI_API_KEY 가 설정되지 않았습니다."
        )
    client = OpenAI(api_key=api_key)
    
    result = client.images.generate(
        model="gpt-image-1",
        prompt=prompt + "image without any letters, neon background",
        size="1024x1024",
        quality="low",
    )

    image_base64 = result.data[0].b64_json
    image_bytes = base64.b64decode(image_base64)

    # Save the image to a file
    with open(output_path, "wb") as f:
        f.write(image_bytes)
    print(f"Image saved to {output_path}")
    return output_path


def main():
    parser = argparse.ArgumentParser(description='Generate an image based on a text prompt')
    parser.add_argument('--prompt', type=str, required=True, help='Text prompt for image generation')
    parser.add_argument('--output', type=str, required=True, help='Output image path')

    args = parser.parse_args()
    try:
        logging.info("작업 시작")
        generate_image(args.prompt, args.output)
        logging.info("작업 완료")
    except Exception as e:
        print(f"Error generating image: {str(e)}")
        traceback.print_exc(file=sys.stderr)
        exit(1)


if __name__ == "__main__":
    main()