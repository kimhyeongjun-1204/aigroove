import argparse
from openai import OpenAI
import base64

def generate_image(prompt, output_path):

    print(f"Generating image with prompt: '{prompt}', size: 1024x1024")
    client = OpenAI(
        api_key="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"
    )
    
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
        generate_image(args.prompt, args.output)
    except Exception as e:
        print(f"Error generating image: {str(e)}")
        exit(1)


if __name__ == "__main__":
    main()