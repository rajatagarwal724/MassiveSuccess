import tempfile
import paramiko
import os
import logging

logger = logging.getLogger(__name__)

def upload_to_sftp(combined_content, directory_date):
    remote_path = SFTP_DIRECTORY
    
    # Write the key to a temporary file
    with tempfile.NamedTemporaryFile(delete=False, mode='w') as key_file:
        key_file.write(SFTP_SECRET_KEY)
        key_path = key_file.name    
    
    transport = paramiko.Transport((SFTP_HOST, 22))
    
    print("Using private key authentication")  # Debug print
    private_key = paramiko.RSAKey.from_private_key_file(key_path)
    transport.connect(username=SFTP_USERNAME, pkey=private_key)
    
    sftp = paramiko.SFTPClient.from_transport(transport)
    try:
        # Changed file extension to .csv instead of .csv.gz
        temp_filename = f'agent_segment_report_{directory_date}.csv'
        temp_filepath = os.path.join(tempfile.gettempdir(), temp_filename)
        logger.info(f"Temp file path: {temp_filepath}")
        
        # Write the content directly to the temporary file without compression
        with open(temp_filepath, 'w') as f:
            f.write(combined_content)
        
        logger.info(f"Created temporary file: {temp_filename}")
        logger.info(f"File Path: {remote_path}{temp_filename}")
        # Upload the uncompressed file to SFTP
        sftp.put(temp_filepath, remote_path + temp_filename)
        logger.info(f"File uploaded successfully to SFTP: {remote_path}{temp_filename}")
        
    except Exception as e:
        logger.error(f"Error during SFTP upload: {e}")
        raise
    finally:
        sftp.close()
        transport.close()
        if os.path.exists(key_path):
            logger.info(f"Cleaning up temporary file: {key_path}")
            os.unlink(key_path)
        # Clean up the temporary file
        if os.path.exists(temp_filepath):
            logger.info(f"Cleaning up temporary file: {temp_filepath}")
            os.unlink(temp_filepath) 