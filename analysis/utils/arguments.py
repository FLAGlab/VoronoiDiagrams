import argparse


def get_args():
    """Get the arguments for the program."""
    parser = argparse.ArgumentParser(
        description='Clone Detection')

    parser.add_argument('--f', nargs='+', type=str, default=[],
                        help='File to anlayze')
    parser.add_argument('--d', nargs='+', type=str, default='',
                        help='Directory of project to analyze')

    return parser.parse_args()
