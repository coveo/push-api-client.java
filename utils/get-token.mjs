import {createAppAuth} from '@octokit/auth-app';
import {setSecret, exportVariable} from '@actions/core'

const auth = createAppAuth({
  appId: process.env.RELEASER_APP_ID,
  privateKey: process.env.RELEASER_PRIVATE_KEY,
  clientId: process.env.RELEASER_CLIENT_ID,
  clientSecret: process.env.RELEASER_CLIENT_SECRET,
});

// Retrieve installation access token
const {token} = await auth({
  type: 'installation',
  installationId: process.env.RELEASER_INSTALLATION_ID,
});

setSecret(token);
exportVariable('RELEASE_TOKEN', token);